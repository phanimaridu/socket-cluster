package open.vm.socket.cluster.utils

import open.vm.socket.cluster.exception.ResourceExistsException
import open.vm.socket.cluster.exception.ResourceNotFoundException
import org.springframework.core.io.ClassPathResource
import org.springframework.util.FileCopyUtils
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths

class Util {
    companion object {

        fun <T> getOrThrowNotFound(name: String, key: String, target : T?) : T {
            return target ?: throw ResourceNotFoundException("$name not found for the $key")
        }

        fun <T> throwIfExists(name : String, key : String, target: T?) {
            if (target != null) throw ResourceExistsException("$name already exists for $key")
        }

        fun getFileContent(path : String) : String{
            if(path.startsWith("classpath:")){
                var classPathResource = ClassPathResource(path.replace("classpath:", ""))
                return String(FileCopyUtils.copyToByteArray(classPathResource.inputStream), Charset.defaultCharset())
            }else{
                return String(Files.readAllBytes(Paths.get(path)), Charset.defaultCharset())
            }
        }
    }
}