package gr.aueb.cf3.tradingjournalapp.controller

import gr.aueb.cf3.tradingjournalapp.TestSpec
import gr.aueb.cf3.tradingjournalapp.service.exceptions.EmailAlreadyExistsException
import gr.aueb.cf3.tradingjournalapp.service.exceptions.UsernameAlreadyExistsException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Subject

class ErrorControllerTest extends TestSpec {
    @Subject
    ErrorController errorController = new ErrorController()

    def "Handle existing username or email exceptions"() {
        given: "exception is thrown"
            def usernameEx = new UsernameAlreadyExistsException("username")
            def emailEx = new EmailAlreadyExistsException("email@aueb.gr")

        when: "username exception is thrown"
            ResponseEntity<?> responseEntity = errorController.handleExistingUsernameOrEmail(usernameEx)

        then: "response status and body are correct"
            responseEntity.statusCode == HttpStatus.CONFLICT
            responseEntity.body == usernameEx.message

        when: "username exception is thrown"
            ResponseEntity<?> responseEntity2 = errorController.handleExistingUsernameOrEmail(emailEx)

        then: "response status and body are correct"
            responseEntity2.statusCode == HttpStatus.CONFLICT
            responseEntity2.body == emailEx.message
    }
}
