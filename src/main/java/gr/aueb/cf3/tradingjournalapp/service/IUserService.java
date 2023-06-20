package gr.aueb.cf3.tradingjournalapp.service;

import gr.aueb.cf3.tradingjournalapp.dto.UserDTO;
import gr.aueb.cf3.tradingjournalapp.model.User;
import gr.aueb.cf3.tradingjournalapp.service.exceptions.EmailAlreadyExistsException;
import gr.aueb.cf3.tradingjournalapp.service.exceptions.UserNotFoundException;
import gr.aueb.cf3.tradingjournalapp.service.exceptions.UsernameAlreadyExistsException;

import java.util.List;

public interface IUserService {
    User findUserById(Long id) throws UserNotFoundException;

    User findUserByUsername(String username) throws UserNotFoundException;

    List<User> findAllUsers();

    User createUser(UserDTO userDTO) throws UsernameAlreadyExistsException, EmailAlreadyExistsException;

    User updateUser(UserDTO userDTO) throws UserNotFoundException;

    void deleteUser(String username) throws UserNotFoundException;
}
