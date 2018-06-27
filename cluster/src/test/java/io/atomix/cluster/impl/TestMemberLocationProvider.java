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
package io.atomix.cluster.impl;

import io.atomix.cluster.MemberLocationEventListener;
import io.atomix.cluster.MemberLocationProvider;
import io.atomix.utils.net.Address;

import java.util.concurrent.CompletableFuture;

/**
 * Test member location provider.
 */
public class TestMemberLocationProvider implements MemberLocationProvider {
  @Override
  public void addListener(MemberLocationEventListener listener) {

  }

  @Override
  public void removeListener(MemberLocationEventListener listener) {

  }

  @Override
  public CompletableFuture<Void> join(Address address) {
    return CompletableFuture.completedFuture(null);
  }

  @Override
  public CompletableFuture<Void> leave() {
    return CompletableFuture.completedFuture(null);
  }
}
