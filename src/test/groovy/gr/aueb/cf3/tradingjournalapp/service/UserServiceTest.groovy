package gr.aueb.cf3.tradingjournalapp.service

import ch.qos.logback.classic.Level
import gr.aueb.cf3.tradingjournalapp.TestSpec
import gr.aueb.cf3.tradingjournalapp.dto.UserDTO
import gr.aueb.cf3.tradingjournalapp.model.Role
import gr.aueb.cf3.tradingjournalapp.model.Statistics
import gr.aueb.cf3.tradingjournalapp.model.Token
import gr.aueb.cf3.tradingjournalapp.model.TokenType
import gr.aueb.cf3.tradingjournalapp.model.Trade
import gr.aueb.cf3.tradingjournalapp.model.User
import gr.aueb.cf3.tradingjournalapp.repository.UserRepository
import gr.aueb.cf3.tradingjournalapp.service.exceptions.EmailAlreadyExistsException
import gr.aueb.cf3.tradingjournalapp.service.exceptions.UserNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Subject

class UserServiceTest extends TestSpec {
    private UserRepository userRepository = Mock(UserRepository)
    private PasswordEncoder passwordEncoder = Mock(PasswordEncoder)
    private JwtService jwtService = Mock(JwtService)

    User user = new User(1L, 'John', 'Doe', 34, 'user01', 'p4ssWord', 'user01@aueb.com', [new Trade(id: 3L)], Role.USER, [new Token(id: 4L, token: "gerwfge632vgh43")], new Statistics(id: 5L, profit: new BigDecimal(34.09)))
    String encodedPassword = 'jdnro348JBILNV87'
    UserDTO userDto = new UserDTO(1L, 'Nick', 'Black', 34, 'user01', 'p4ssWord', 'updatedUser01@aueb.com')

    @Subject
    IUserService userService = new UserServiceImpl(userRepository, passwordEncoder, jwtService)

    def "Happy path - Find User By Id"() {
        when: 'findUserById is invoked'
            User resultUser = userService.findUserById(user.id)

        then: "a user exist in our db"
            1 * userRepository.findById(user.id) >> Optional.of(user)
            0 * _

        and: "the resultUser is the same as the expectedUser"
            resultUser == user
    }

    def "Unhappy path - find User By Id don't find a user"() {
        when: 'findUserById is invoked'
            userService.findUserById(user.id)

        then: "a user do not exist in our db"
            1 * userRepository.findById(user.id) >> Optional.ofNullable(null)
            0 * _

        and: "log prompted and exception is thrown"
            assertLog(Level.WARN, "User with id $user.id not found")
            def e = thrown(UserNotFoundException)
            e.message == "User with id: $user.id not found"

    }

    def "Happy path - Find User By Username"() {
        when: 'findUserByUsername is invoked'
            User resultUser = userService.findUserByUsername(user.username)

        then: "a user exist in our db"
            1 * userRepository.findUserByUsername(user.username) >> user
            0 * _

        and: "the resultUser is the same as the expectedUser"
            resultUser == user
    }

    def "Unhappy path - find User By Username don't find a user"() {
        when: 'findUserByUsername is invoked'
            userService.findUserByUsername(user.username)

        then: "a user do not exist in our db"
            1 * userRepository.findUserByUsername(user.username) >> null
            0 * _

        and: "log prompted and exception is thrown"
            assertLog(Level.WARN, "User with username $user.username not found")
            def e = thrown(UserNotFoundException)
            e.message == "User with Username: $user.username not found"
    }

    def "Happy path - Find all Users"() {
        when: 'findAllUsers is invoked'
            List<User> resultUsersList = userService.findAllUsers()

        then: "a user exist in our db"
            1 * userRepository.findAll() >> [user, user]
            0 * _

        and: "the resultUser is the same as the expectedUser"
            resultUsersList == [user, user]
    }

    def "Happy path - Update User"() {
        given: 'the expected user after update'
            User expectedUser = new User(1L, 'Nick', 'Black', 34, 'user01', encodedPassword, 'updatedUser01@aueb.com', [new Trade(id: 3L)], Role.USER, [new Token(id: 4L, token: "gerwfge632vgh43")], new Statistics(id: 5L, profit: new BigDecimal(34.09)))

        when: 'updateUser is invoked'
            User resultUser = userService.updateUser(userDto)

        then: "a user exist in our db"
            1 * passwordEncoder.encode(userDto.password) >> encodedPassword
            1 * userRepository.findByUsername(userDto.username) >> Optional.of(user)
            1 * userRepository.isEmailExists(userDto.email) >> false
            1 * userRepository.save(expectedUser) >> expectedUser
            0 * _

        and: "the resultUser is the same as the expectedUser"
            resultUser == expectedUser
    }

    def "Unhappy path - Update User throws UserNotFoundException"() {
        when: 'updateUser is invoked'
            userService.updateUser(userDto)

        then: "a user not found in our db"
            1 * passwordEncoder.encode(userDto.password) >> encodedPassword
            1 * userRepository.findByUsername(userDto.username) >> Optional.ofNullable(null)
            0 * _

        and: "log prompted and exception is thrown"
            assertLog(Level.ERROR, "Update canceled User $userDto.username do not exist")
            def e = thrown(UserNotFoundException)
            e.message == "User with Username: $userDto.username not found"
    }

    def "Unhappy path - Update User throws EmailAlreadyExistsException"() {
        when: 'updateUser is invoked'
            userService.updateUser(userDto)

        then: "the user has new email that already exists in our db "
            1 * passwordEncoder.encode(userDto.password) >> encodedPassword
            1 * userRepository.findByUsername(userDto.username) >> Optional.of(user)
            1 * userRepository.isEmailExists(userDto.email) >> true
            0 * _

        and: "log prompted and exception is thrown"
            assertLog(Level.ERROR, "Update canceled User with email $userDto.email already exist")
            def e = thrown(EmailAlreadyExistsException)
            e.message == "User with email '${userDto.email}' already exist"
    }

    def "Happy path - Delete User"() {
        when: 'deleteUser is invoked'
            userService.deleteUser(user.username)

        then: "a user exist in our db"
            1 * userRepository.findUserByUsername(userDto.username) >> user
            1 * userRepository.deleteById(user.id)
            1 * jwtService.revokeAllUserTokens(user, TokenType.BEARER_ACCESS)
            1 * jwtService.revokeAllUserTokens(user, TokenType.BEARER_REFRESH)
            0 * _
    }

    def "Unhappy path - Delete User Throws UserNotFoundException"() {
        when: 'deleteUser is invoked'
            userService.deleteUser(user.username)

        then: "a user does not exist in our db"
            1 * userRepository.findUserByUsername(userDto.username) >> null
            0 * _

        and: "log prompted and exception is thrown"
            assertLog(Level.ERROR, "Delete user failed user with username $user.username not found")
            def e = thrown(UserNotFoundException)
            e.message == "User with Username: $user.username not found"
    }

}
