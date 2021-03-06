/*
 * Copyright 2018 Fundação CERTI
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */
package certi.org.br.jocdandroidtestapp;

import java.util.HashMap;

public class AsyncResponse {

  public interface ListBoards {

    void processAsyncTaskFinish(HashMap<String, String> boards);

    void processAsyncTaskUpdate(String status);
  }

  public interface FlashBoard {

    void processAsyncTaskFinish(String result);

    void processAsyncTaskUpdate(String status);
  }
}
