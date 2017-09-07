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

import info.gianlucacosta.mytasks.backends.Caching;
import info.gianlucacosta.mytasks.model.users.User;
import info.gianlucacosta.mytasks.model.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;

/**
 * Authenticates users by checking the (userName, password) token
 */
@Service
public class BasicAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    @Caching
    private UserRepository userRepository;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String userName = authentication.getName();
        String password = authentication.getCredentials().toString();

        boolean userAuthenticated =
                userRepository
                        .findByName(userName)
                        .map(foundUser ->
                                Objects.equals(foundUser.getPassword(), password)
                        )
                        .orElse(false);

        return
                userAuthenticated ?

                        new UsernamePasswordAuthenticationToken(
                                userName,
                                password,
                                Objects.equals(userName, User.ADMIN_NAME) ?
                                        Collections.singletonList(
                                                new SimpleGrantedAuthority(User.ADMIN_NAME)
                                        )
                                        :
                                        Collections.emptyList())
                        :

                        null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication == UsernamePasswordAuthenticationToken.class;
    }
}
