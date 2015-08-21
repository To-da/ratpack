/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ratpack.exec;

import ratpack.func.Action;

@FunctionalInterface
public interface ExecInitializer {

  /**
   * Called before the execution is started in order to perform any initialisation.
   * <p>
   * Implementations of this method typically add objects to the execution (as it's a registry).
   * <p>
   * This is called before {@link ExecBuilder#start(Action)}, but after {@link ExecBuilder#onStart(Action)}.
   *
   * @param execution the execution that is about to be started.
   */
  void init(Execution execution);

}
