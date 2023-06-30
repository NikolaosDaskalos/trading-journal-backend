package gr.aueb.cf3.tradingjournalapp.service;

import gr.aueb.cf3.tradingjournalapp.dto.UserDTO;
import gr.aueb.cf3.tradingjournalapp.model.User;
import gr.aueb.cf3.tradingjournalapp.service.exceptions.EmailAlreadyExistsException;
import gr.aueb.cf3.tradingjournalapp.service.exceptions.UserNotFoundException;

import java.util.List;

public interface IUserService {
    User findUserByUsername(String username) throws UserNotFoundException;

    List<User> findAllUsers();

    User updateUser(UserDTO userDTO) throws UserNotFoundException, EmailAlreadyExistsException;

    void deleteUser(String username) throws UserNotFoundException;
}
