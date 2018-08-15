/*
 * Copyright 2018-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.atomix.core.multimap;

import io.atomix.primitive.protocol.ProxyProtocol;
import io.atomix.protocols.backup.MultiPrimaryProtocol;

/**
 * Raft multimap test.
 */
public class PrimaryBackupAtomicMultimapTest extends AtomicMultimapTest {
  @Override
  protected ProxyProtocol protocol() {
    return MultiPrimaryProtocol.builder("primary-backup")
        .withBackups(2)
        .withMaxRetries(5)
        .build();
  }
}
