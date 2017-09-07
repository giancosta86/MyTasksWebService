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

package info.gianlucacosta.mytasks.backends.memory.users;

import info.gianlucacosta.mytasks.backends.Caching;
import info.gianlucacosta.mytasks.backends.Persistent;
import info.gianlucacosta.mytasks.model.users.User;
import info.gianlucacosta.mytasks.model.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Caching
public class ConcurrentHashMapUserRepository implements UserRepository {
    private final Map<String, User> userMap =
            new ConcurrentHashMap<>();

    @Autowired
    @Persistent
    private UserRepository persistentRepository;

    @Override
    public List<User> findAll() {
        return persistentRepository.findAll();
    }

    @Override
    public Optional<User> findByName(String name) {
        return Optional.ofNullable(
                userMap
                        .computeIfAbsent(
                                name,
                                userName ->
                                        persistentRepository
                                                .findByName(userName)
                                                .orElse(null)
                        )
        );
    }


    @Override
    public void add(User user) {
        persistentRepository.add(user);

        userMap.put(user.getName(), user);
    }


    @Override
    public void update(User user) {
        persistentRepository.update(user);

        userMap.put(user.getName(), user);
    }


    @Override
    public void removeByName(String userName) {
        persistentRepository.removeByName(userName);

        userMap.remove(userName);
    }
}
