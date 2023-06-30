package gr.aueb.cf3.tradingjournalapp.service;

import gr.aueb.cf3.tradingjournalapp.dto.AuthDTO;
import gr.aueb.cf3.tradingjournalapp.dto.LoginDTO;
import gr.aueb.cf3.tradingjournalapp.dto.UserDTO;
import gr.aueb.cf3.tradingjournalapp.service.exceptions.EmailAlreadyExistsException;
import gr.aueb.cf3.tradingjournalapp.service.exceptions.UsernameAlreadyExistsException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ILoginService {
    AuthDTO register(UserDTO userDTO) throws EmailAlreadyExistsException, UsernameAlreadyExistsException;

    AuthDTO login(LoginDTO loginDTO);

    AuthDTO refreshToken(HttpServletRequest request, HttpServletResponse response);
}
