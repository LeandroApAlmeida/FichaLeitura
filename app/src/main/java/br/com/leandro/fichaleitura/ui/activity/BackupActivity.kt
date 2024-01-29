package br.com.leandro.fichaleitura.ui.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import br.com.leandro.fichaleitura.database.AppDatabase
import br.com.leandro.fichaleitura.database.BackupManager
import br.com.leandro.fichaleitura.databinding.ActivityBackupBinding
import br.com.leandro.fichaleitura.security.CipherListener
import br.com.leandro.fichaleitura.utils.dateToText
import br.com.leandro.fichaleitura.utils.getSystemTime
import br.com.leandro.fichaleitura.utils.hourToText
import java.util.Date
import kotlin.system.exitProcess

class BackupActivity : AppCompatActivity(), CipherListener {


    private lateinit var binding: ActivityBackupBinding
    private lateinit var backupManager: BackupManager
    private var processingData: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        backupManager = BackupManager()

        binding = ActivityBackupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackupFindFile.setOnClickListener { onFindBackupFileButtonClick(it) }

        supportActionBar?.title = "Backup/Restauração"

        binding.rbBackupModeBackup.isChecked = true
        binding.txtBackupFilePath.isVisible = false
        binding.pgbBackupStatus.isVisible = false

    }


    private var resultBackup = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.also { uri ->
                Thread {
                    try {
                        runOnUiThread { binding.txtBackupFilePath.text = uri.path }
                        backupManager.doBackup(uri, this)
                        runOnUiThread {
                            with(AlertDialog.Builder(this)) {
                                setTitle("Concluído!")
                                setMessage("Backup concluído com sucesso.")
                                setPositiveButton("OK", null)
                                show()
                            }
                        }
                    } catch (ex: Exception) {
                        runOnUiThread {
                            with(AlertDialog.Builder(this)) {
                                setTitle("Erro")
                                setMessage(ex.message)
                                setPositiveButton("OK", null)
                                show()
                            }
                        }
                    }
                }.start()
            }
        }
    }


    private fun showBackupFileDialog() {
        val date = Date(getSystemTime())
        val dateFields = dateToText(date).split("/")
        val dateStr = dateFields[2] + dateFields[1] + dateFields[0]
        val fileName = "FichaLeitura_" + dateStr + "_" + hourToText(date).replace(":", "") + ".backup"
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            type = "application/backup"
            putExtra(Intent.EXTRA_TITLE, fileName)
        }
        resultBackup.launch(intent)
    }


    private fun restoreDatabase(restoreFileUri: Uri) {
        val owner = this
        Thread {
            try {
                AppDatabase.instance.disconnect()
                backupManager.doRestore(restoreFileUri, this)
                val packageManager: PackageManager = this.packageManager
                val activity = packageManager.getLaunchIntentForPackage(this.packageName)
                if (activity != null) {
                    activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    this.applicationContext.startActivity(activity)
                    exitProcess(0)
                }
            } catch (ex: Exception) {
                runOnUiThread {
                    with(AlertDialog.Builder(owner)) {
                        setTitle("Erro")
                        setMessage(ex.message)
                        setPositiveButton("OK", null)
                        show()
                    }
                }
            }
        }.start()
    }


    private var resultRestore = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.also { uri ->
                val sb: StringBuilder = StringBuilder()
                sb.append("Confirma a restauração do banco de dados? Todos os dados ")
                sb.append("no banco de dados atual serão apagados de forma a não ser ")
                sb.append("mais possível a recuperação posterior.")
                with (AlertDialog.Builder(this)) {
                    setTitle("Atenção!")
                    setMessage(sb.toString())
                    setPositiveButton("Sim") { _, _ ->
                        runOnUiThread { binding.txtBackupFilePath.text = uri.path }
                        restoreDatabase(uri)
                    }
                    setNegativeButton("Não", null)
                    show()
                }
            }
        }
    }


    private fun openBackupFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "*/*" }
        resultRestore.launch(intent)
    }


    private fun onFindBackupFileButtonClick(view: View) {
        if (binding.rbBackupModeBackup.isChecked) {
            showBackupFileDialog()
        } else {
            openBackupFile()
        }
    }


    override fun onStartProcess(streamLength: Int) {
        runOnUiThread {
            binding.txtBackupFilePath.isVisible = true
            binding.pgbBackupStatus.isVisible = true
            processingData = 0
            binding.pgbBackupStatus.min = 0
            binding.pgbBackupStatus.max = streamLength
            binding.pgbBackupStatus.progress = 0
        }
    }


    override fun onProcessData(dataLength: Int) {
        this.processingData += dataLength
        binding.pgbBackupStatus.progress = processingData
    }


    override fun onEndProcess(status: Int) {
        runOnUiThread {
            binding.txtBackupFilePath.isVisible = false
            binding.pgbBackupStatus.isVisible = false
        }
    }


}