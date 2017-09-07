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

package info.gianlucacosta.mytasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.gianlucacosta.mytasks.backends.Caching;
import info.gianlucacosta.mytasks.model.users.User;
import info.gianlucacosta.mytasks.model.users.UserRepository;
import info.gianlucacosta.mytasks.webservice.BasicAuthenticationProvider;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Base64Utils;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.BDDMockito.given;

/**
 * Base class for MVC tests
 */
@Import({
        WebSecurityConfig.class,
        BasicAuthenticationProvider.class
})
public abstract class MvcTestBase {
    @Autowired
    protected MockMvc mvc;

    protected ObjectMapper jsonMapper;

    @MockBean
    @Caching
    protected UserRepository userRepository;

    protected final User adminUser =
            new User(User.ADMIN_NAME, User.ADMIN_NAME);

    protected final User standardUser =
            new User("alpha", "a");


    @Before
    public void init() {
        jsonMapper =
                new ObjectMapper();

        given(userRepository.findAll())
                .willReturn(Arrays.asList(adminUser, standardUser));

        given(userRepository.findByName(adminUser.getName()))
                .willReturn(Optional.of(adminUser));

        given(userRepository.findByName(standardUser.getName()))
                .willReturn(Optional.of(standardUser));
    }


    protected String getBasicAuthenticationHeader(User user) {
        return getBasicAuthenticationHeader(user.getName(), user.getPassword());
    }


    protected String getBasicAuthenticationHeader(String userName, String password) {
        String plainAuthentication =
                String.format("%s:%s", userName, password);

        String encodedAuthentication =
                new String(
                        Base64Utils.encode(
                                plainAuthentication
                                        .getBytes()
                        )
                );


        return "Basic " + encodedAuthentication;
    }
}
