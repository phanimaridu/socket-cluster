package open.vm.socket.cluster.config

import open.vm.socket.cluster.exception.ResourceExistsException
import open.vm.socket.cluster.exception.ResourceNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus


@ControllerAdvice
class GlobalExceptionHandler {

    //TODO:How to kotlin specific logger
    companion object {
        private var LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    fun handleResourceNotFoundException(e: ResourceNotFoundException): Map<String, String?> {
        LOGGER.error("${e.message}")
        return mapOf("message" to e.message)
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    fun handleResourceExistsException(e: ResourceExistsException): Map<String, String?> {
        LOGGER.error("${e.message}")
        return mapOf("message" to e.message)
    }

}