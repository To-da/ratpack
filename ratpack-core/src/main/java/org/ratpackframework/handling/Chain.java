/*
 * Copyright 2013 the original author or authors.
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

package org.ratpackframework.handling;

import org.ratpackframework.api.Nullable;
import org.ratpackframework.registry.Registry;
import org.ratpackframework.util.Action;

import java.util.List;

/**
 * A {@code Chain} can be used to build a linked series of {@code Handlers}.
 * <p>
 * The {@code Chain} type does not represent the handlers "in action".
 * That is, it is the construction of a handler chain.
 *
 * @see Handlers#chain(org.ratpackframework.util.Action)
 */
public interface Chain {

  /**
   * Adds the given {@code Handler} to this {@code Chain}.
   *
   * @param handler the {@code Handler} to add
   * @return this {@code Chain}
   */
  Chain handler(Handler handler);

  /**
   * Adds a {@code Handler} to this {@code Chain} that delegates to the given handlers if the
   * relative path starts with the given {@code prefix}.
   * <p>
   * All path based handlers become relative to the given {@code prefix}.
   * <pre>
   *   prefix("person/:id") {
   *     get("info") {
   *       // e.g. /person/2/info
   *     }
   *     post("save") {
   *       // e.g. /person/2/save
   *     }
   *     prefix("child/:childId") {
   *       get("info") {
   *         // e.g. /person/2/child/1/info
   *       }
   *     }
   *   }
   * </pre>
   * <p>
   * See {@link org.ratpackframework.handling.Handlers#prefix(String, java.util.List)}
   * for format details on the {@code prefix} string.
   *
   * @param prefix the relative path to match on
   * @param handlers the handlers to delegate to
   * @return this {@code Chain}
   */
  Chain prefix(String prefix, Handler... handlers);

  /**
   * Adds a {@code Handler} to this {@code Chain} that delegates to the given handlers if the
   * relative path starts with the given {@code prefix}.
   * <p>
   * See {@link Chain#prefix(String, Handler...)} for more details.
   *
   * @param prefix the relative path to match on
   * @param handlers the handlers to delegate to
   * @return this {@code Chain}
   */
  Chain prefix(String prefix, List<Handler> handlers);

  /**
   * Adds a {@code Handler} to this {@code Chain} that delegates to the given chain if the
   * relative path starts with the given {@code prefix}.
   * <p>
   * See {@link Chain#prefix(String, Handler...)} for more details.
   *
   * @param prefix the relative path to match on
   * @param builder the definition of the chain to delegate to
   * @return this {@code Chain}
   */
  Chain prefix(String prefix, Action<? super Chain> builder);

  /**
   * Adds a {@code Handler} to this {@code Chain} that delegates to the given {@code Handler} if the relative {@code path}
   * matches the given {@code path} exactly.
   * <p>
   * Nesting {@code path} handlers will not work due to the exact matching, use a combination of {@code path}
   * and {@code prefix} instead.  See {@link Chain#prefix(String, Handler...)} for details.
   * <pre>
   *   // this will not work
   *   path("person/:id") {
   *     path("child/:childId") {
   *       // a request of /person/2/child/1 will not get passed the first handler as it will try
   *       // to match "person/2/child/1" with "person/2" which does not match
   *     }
   *
   *   // this will work
   *   prefix("person/:id") {
   *     path("child/:childId") {
   *       // a request of /person/2/child/1 will work this time
   *     }
   *   }
   * </pre>
   * <p>
   * See {@link Handlers#path(String, List)} for the details on how {@code path} is interpreted.
   *
   * @param path the relative path to match exactly on
   * @param handler the handler to delegate to
   * @return this {@code Chain}
   * @see Chain#post(String, Handler)
   * @see Chain#get(String, Handler)
   */
  Chain path(String path, Handler handler);

  /**
   * Adds a {@code Handler} to this {@code Chain} that delegates to the given {@code Handler}
   * if the relative {@code path} matches the given {@code path} and the {@code request}
   * {@code HTTPMethod} is {@code GET}.
   * <p>
   * See {@link Handlers#get(String, Handler)} for more details on the {@code Handler} created
   *
   * @param path the relative path to match on
   * @param handler the handler to delegate to
   * @return this {@code Chain}
   * @see Chain#post(String, Handler)
   * @see Chain#path(String, Handler)
   */
  Chain get(String path, Handler handler);

  /**
   * Adds a {@code Handler} to this {@code Chain} that delegates to the given {@code Handler}
   * if the {@code request} {@code HTTPMethod} is {@code GET} and the {@code path} is at the
   * current root.
   * <p>
   * See {@link Handlers#get(Handler)} for more details on the {@code Handler} created
   *
   * @param handler the handler to delegate to
   * @return this {@code Chain}
   * @see Chain#post(Handler)
   */
  Chain get(Handler handler);

  /**
   * Adds a {@code Handler} to this {@code Chain} that delegates to the given {@code Handler} if
   * the relative {@code path} matches the given {@code path} and the {@code request} {@code HTTPMethod}
   * is {@code POST}.
   * <p>
   * See {@link Handlers#post(String, Handler)} for more details on the {@code Handler} created
   *
   * @param path the relative path to match on
   * @param handler the handler to delegate to
   * @return this {@code Chain}
   * @see Chain#get(String, Handler)
   * @see Chain#path(String, Handler)
   */
  Chain post(String path, Handler handler);

  /**
   * Adds a {@code Handler} to this {@code Chain} that delegates to the given {@code Handler} if
   * the {@code request} {@code HTTPMethod} is {@code POST} and the {@code path} is at the current root.
   * <p>
   * See {@link Handlers#post(Handler)} for more details on the {@code Handler} created
   *
   * @param handler the handler to delegate to
   * @return this {@code Chain}
   * @see Chain#get(Handler)
   */
  Chain post(Handler handler);

  /**
   * Adds a {@code Handler} to this {@code Chain} that serves static assets at the given file system path,
   * relative to the contextual file system binding.
   * <p>
   * See {@link Handlers#assets(String, String...)} for more details on the {@code Handler} created
   * <pre>
   *    prefix("foo") {
   *      assets("d1", "index.html", "index.xhtml")
   *    }
   * </pre>
   * In the above configuration a request like "/foo/app.js" will return the static file "app.js" that is
   * located in the directory "d1".
   * <p>
   * If the request matches a directory e.g. "/foo", an index file may be served.  The {@code indexFiles}
   * array specifies the names of files to look for in order to serve.
   *
   * @param path the relative path to the location of the assets to serve
   * @param indexFiles the index files to try if the request is for a directory
   * @return this {@code Chain}
   */
  Chain assets(String path, String... indexFiles);

  /**
   * Adds a {@code Handler} to this {@code Chain} that inserts the given handlers with the given {@code service} addition.
   * <p>
   * The {@code service} object will be available by its concrete type.
   * <p>
   * See {@link Handlers#register(Object, java.util.List)} for more details on the {@code Handler} created
   *
   * @param service the object to add to the service for the handlers
   * @param handlers the handlers to register the service with
   * @return this {@code Chain}
   * @see Chain#register(Class, Object, java.util.List)
   */
  Chain register(Object service, List<Handler> handlers);

  /**
   * Adds a {@code Handler} to this {@code Chain} that inserts the given handlers with the given {@code service} addition.
   * <p>
   * The {@code service} object will be available by the given type.
   * <p>
   * See {@link Handlers#register(Class, Object, java.util.List)} for more details on the {@code Handler} created
   *
   * @param type the {@code Type} by which to make the service object available
   * @param service the object to add to the service for the handlers
   * @param handlers the handlers to register the service with
   * @param <T> the concrete type of the service addition
   * @return this {@code Chain}
   * @see Chain#register(Object, java.util.List)
   */
  <T> Chain register(Class<? super T> type, T service, List<Handler> handlers);

  /**
   * Adds a {@code Handler} to this {@code Chain} that changes the {@link org.ratpackframework.file.FileSystemBinding}
   * for the given handlers.
   * <p>
   * See {@link Handlers#fileSystem(String, java.util.List)} for more details on the {@code Handler} created
   *
   * @param path the relative {@code path} to the new file system binding point
   * @param handlers the definition of the handler chain
   * @return this {@code Chain}
   */
  Chain fileSystem(String path, List<Handler> handlers);

  /**
   * The registry that backs this {@code Chain}.
   * <p>
   * The registry that is available is dependent on how the {@code Chain} was constructed.
   *
   * @return The registry that backs this {@code Chain}, or {@code null} if this {@code Chain} has no registry.
   */
  @Nullable
  Registry<Object> getRegistry();

}
