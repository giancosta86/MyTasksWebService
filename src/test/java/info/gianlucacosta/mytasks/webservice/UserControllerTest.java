/*^
  ===========================================================================
  MyTasksWebService
  ===========================================================================
  Copyright (C) 2017 Gianluca Costa
  ===========================================================================
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  ===========================================================================
*/

package info.gianlucacosta.mytasks.webservice;

import info.gianlucacosta.mytasks.MvcTestBase;
import info.gianlucacosta.mytasks.model.users.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.ServletException;
import java.util.Arrays;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest extends MvcTestBase {
    @Test
    public void listingUsersShouldWork() throws Exception {
        this.mvc.perform(
                get(UserController.path + "/")
                        .header(
                                "Authorization",
                                getBasicAuthenticationHeader(adminUser)
                        )
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        jsonMapper.writeValueAsString(
                                Arrays.asList(
                                        adminUser.getName(),
                                        standardUser.getName()
                                )
                        )
                        )
                );

        verify(userRepository).findAll();
    }


    @Test
    public void creatingNewUsersShouldWork() throws Exception {
        User userToCreate = new User("beta", "b");

        this.mvc.perform(
                post(UserController.path + "/")
                        .header(
                                "Authorization",
                                getBasicAuthenticationHeader(adminUser)
                        )
                        .param("name", userToCreate.getName())
                        .param("password", userToCreate.getPassword())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(userRepository).add(userToCreate);
    }


    @Test(expected = ServletException.class)
    public void creatingNewUsersWhoseNameContainsOnlySpacesShouldFail() throws Exception {
        this.mvc.perform(
                post(UserController.path + "/")
                        .header(
                                "Authorization",
                                getBasicAuthenticationHeader(adminUser)
                        )
                        .param("name", "       ")
                        .param("password", " test")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(userRepository, times(0)).add(Mockito.any());
    }


    @Test
    public void editingUsersShouldWork() throws Exception {
        User updatedUser = new User(
                standardUser.getName(),
                standardUser.getPassword() + "X"
        );

        this.mvc.perform(
                put(UserController.path + "/")
                        .header(
                                "Authorization",
                                getBasicAuthenticationHeader(adminUser)
                        )
                        .param("name", updatedUser.getName())
                        .param("password", updatedUser.getPassword())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(userRepository).update(updatedUser);
    }


    @Test
    public void deletingUsersShouldWork() throws Exception {
        this.mvc.perform(
                delete(UserController.path + "/")
                        .header(
                                "Authorization",
                                getBasicAuthenticationHeader(adminUser)
                        )
                        .param("name", standardUser.getName())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(userRepository).removeByName(standardUser.getName());
    }


    @Test
    public void listingUsersShouldBeForbiddenToStandardUsers() throws Exception {
        this.mvc.perform(
                get(UserController.path + "/")
                        .header(
                                "Authorization",
                                getBasicAuthenticationHeader(standardUser)
                        )
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}