package com.ckweb.rest_api.service.interfaces;

import java.util.List;

import com.ckweb.rest_api.dto.user.UserAutoUpdateDTO;
import com.ckweb.rest_api.dto.user.UserGetDTO;
import com.ckweb.rest_api.dto.user.UserUpdateDTO;
import com.ckweb.rest_api.model.User;

public interface UserServiceInterface {
    public List<UserGetDTO> findAll();
    public UserGetDTO findById(Long id);
    public UserGetDTO getUsuarioLogadoInfo(String token);
    public User save(User user);
    public UserGetDTO update(UserUpdateDTO dto);
    public UserGetDTO updateUsuarioLogadoInfo(String token, UserAutoUpdateDTO dto);
    public void delete(Long id);
    public void deleteUsuarioLogadoInfo(String token);
}
