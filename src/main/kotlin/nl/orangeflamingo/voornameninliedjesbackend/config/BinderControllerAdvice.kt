package nl.orangeflamingo.voornameninliedjesbackend.config

import org.springframework.core.annotation.Order
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.InitBinder

@ControllerAdvice
@Order(10000)
class BinderControllerAdvice {

    // Prevent Remote Code Execution (Spring4Shell)
    // See https://www.lunasec.io/docs/blog/spring-rce-vulnerabilities/ for more info
    @InitBinder
    fun setAllowedFields(dataBinder: WebDataBinder) {
        dataBinder.setDisallowedFields("class.*", "Class.*", "*.class.*", "*.Class.*")
    }
}