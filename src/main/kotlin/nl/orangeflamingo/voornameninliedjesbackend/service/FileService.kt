package nl.orangeflamingo.voornameninliedjesbackend.service

import nl.orangeflamingo.voornameninliedjesbackend.utils.Utils
import org.apache.commons.io.FileUtils
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException
import java.io.InputStream
import kotlin.jvm.Throws

@Service
class FileService {

    @Throws(IOException::class)
    fun writeToDisk(remoteUrl: String, localUrl: String) {
        val imageUrl = Utils.resourceAsInputStream(remoteUrl)
        val inputS: InputStream = imageUrl.openStream()
        val targetFile = File(localUrl)
        FileUtils.copyInputStreamToFile(inputS, targetFile)
    }

    fun fileExists(localUrl: String): Boolean {
        return File(localUrl).exists()
    }
}