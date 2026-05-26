package com.sop.backend.mapper;

import com.sop.backend.dto.SearchUserDTO;
import com.sop.backend.models.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SearchUserDtoMapper {

     public List<SearchUserDTO> toList (List<User> users) {
         List<SearchUserDTO> searchUserDTOs = new ArrayList<>();

         for (User user : users) {
             searchUserDTOs.add(toDto(user));
         }

         return searchUserDTOs;
     }

     public SearchUserDTO toDto (User user) {
         return new SearchUserDTO(user.getId(), user.getName());
     }
}
