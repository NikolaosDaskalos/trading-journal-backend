package gr.aueb.cf3.tradingjournalapp.controller

import gr.aueb.cf3.tradingjournalapp.TestSpec
import gr.aueb.cf3.tradingjournalapp.dto.AuthDTO
import gr.aueb.cf3.tradingjournalapp.dto.LoginDTO
import gr.aueb.cf3.tradingjournalapp.dto.UserDTO
import gr.aueb.cf3.tradingjournalapp.service.ILoginService
import gr.aueb.cf3.tradingjournalapp.service.LoginServiceImpl
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Subject

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class LoginControllerTest extends TestSpec {
    ILoginService loginService = Mock(LoginServiceImpl)
    HttpServletRequest servletRequest = Mock(HttpServletRequest)
    HttpServletResponse servletResponse = Mock(HttpServletResponse)

    @Subject
    LoginController loginController = new LoginController(loginService)

    LoginDTO loginDTO = new LoginDTO("testuser", "testpassword")
    AuthDTO authDTO = new AuthDTO("someToken", "someRefreshToken")
    UserDTO userDTO = new UserDTO(1L, 'John', 'Doe', 30, 'testUser', '1234', 'john@aueb.gr')

    def "Happy Path - user register successfully"() {
        when: "register is invoked"
            ResponseEntity<AuthDTO> response = loginController.register(userDTO)

        then: "register service is called"
            1 * loginService.register(userDTO) >> authDTO
            0 * _

        and: "response is correct"
            response.statusCode == HttpStatus.OK
            response.body == authDTO
    }

    def "Happy Path - successful login"() {
        when: "login is invoked"
            ResponseEntity<AuthDTO> response = loginController.login(loginDTO)

        then: "login service is called"
            1 * loginService.login(loginDTO) >> authDTO
            0 * _

        and: "response is correct"
            response.statusCode == HttpStatus.OK
            response.body == authDTO
    }

    def "Happy Path - refresh token"() {
        when: "refresh token is invoked"
            ResponseEntity<AuthDTO> response = loginController.refreshToken(servletRequest, servletResponse)

        then: "register service is called"
            1 * loginService.refreshToken(servletRequest, servletResponse) >> authDTO
            0 * _

        and: "response is correct"
            response.statusCode == HttpStatus.OK
            response.body == authDTO
    }
}
