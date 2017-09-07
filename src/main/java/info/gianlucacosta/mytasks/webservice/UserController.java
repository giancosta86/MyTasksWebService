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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping(UserController.path)
public class UserController {
    public static final String path = "/users";

    @Autowired
    @Caching
    private UserRepository userRepository;


    @GetMapping("/")
    public List<String> getUserNames() {
        return userRepository
                .findAll()
                .stream()
                .map(User::getName)
                .collect(Collectors.toList());
    }


    @PostMapping("/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addUser(
            @RequestParam String name,
            @RequestParam String password
    ) {
        User user =
                new User(name, password);

        userRepository.add(user);
    }


    @PutMapping("/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUser(
            @RequestParam String name,
            @RequestParam String password
    ) {
        User user =
                new User(name, password);

        userRepository.update(user);
    }


    @DeleteMapping("/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeUser(
            @RequestParam String name
    ) {
        userRepository.removeByName(name);
    }
}
