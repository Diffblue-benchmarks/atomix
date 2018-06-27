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
package io.atomix.cluster;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

/**
 * Atomix cluster test.
 */
public class AtomixClusterTest {

  @Test
  public void testMembers() throws Exception {
    Collection<Member> members = Arrays.asList(
        Member.builder("foo")
            .withAddress("localhost:5000")
            .build(),
        Member.builder("bar")
            .withAddress("localhost:5001")
            .build(),
        Member.builder("baz")
            .withAddress("localhost:5002")
            .build());

    AtomixCluster cluster1 = AtomixCluster.builder()
        .withLocalMember("foo")
        .withMembers(members)
        .build();
    cluster1.start().join();

    assertEquals("foo", cluster1.getMembershipService().getLocalMember().id().id());

    AtomixCluster cluster2 = AtomixCluster.builder()
        .withLocalMember("bar")
        .withMembers(members)
        .build();
    cluster2.start().join();

    assertEquals("bar", cluster2.getMembershipService().getLocalMember().id().id());

    AtomixCluster cluster3 = AtomixCluster.builder()
        .withLocalMember("baz")
        .withMembers(members)
        .build();
    cluster3.start().join();

    assertEquals("baz", cluster3.getMembershipService().getLocalMember().id().id());

    List<CompletableFuture<Void>> futures = Stream.of(cluster1, cluster2, cluster3).map(AtomixCluster::stop)
        .collect(Collectors.toList());
    try {
      CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();
    } catch (Exception e) {
      // Do nothing
    }
  }

  @Test
  public void testDiscovery() throws Exception {
    AtomixCluster cluster1 = AtomixCluster.builder()
        .withLocalMember(Member.member("localhost:5000"))
        .withMulticastEnabled()
        .build();
    AtomixCluster cluster2 = AtomixCluster.builder()
        .withLocalMember(Member.member("localhost:5001"))
        .withMulticastEnabled()
        .build();
    AtomixCluster cluster3 = AtomixCluster.builder()
        .withLocalMember(Member.member("localhost:5002"))
        .withMulticastEnabled()
        .build();

    TestClusterMembershipEventListener listener1 = new TestClusterMembershipEventListener();
    cluster1.getMembershipService().addListener(listener1);
    TestClusterMembershipEventListener listener2 = new TestClusterMembershipEventListener();
    cluster2.getMembershipService().addListener(listener2);
    TestClusterMembershipEventListener listener3 = new TestClusterMembershipEventListener();
    cluster3.getMembershipService().addListener(listener3);

    List<CompletableFuture<Void>> startFutures = Stream.of(cluster1, cluster2, cluster3).map(AtomixCluster::start)
        .collect(Collectors.toList());
    CompletableFuture.allOf(startFutures.toArray(new CompletableFuture[startFutures.size()])).get(10, TimeUnit.SECONDS);

    assertEquals(ClusterMembershipEvent.Type.MEMBER_ADDED, listener1.nextEvent().type());
    assertEquals(ClusterMembershipEvent.Type.MEMBER_ADDED, listener1.nextEvent().type());
    assertEquals(ClusterMembershipEvent.Type.MEMBER_ADDED, listener2.nextEvent().type());
    assertEquals(ClusterMembershipEvent.Type.MEMBER_ADDED, listener2.nextEvent().type());
    assertEquals(ClusterMembershipEvent.Type.MEMBER_ADDED, listener3.nextEvent().type());
    assertEquals(ClusterMembershipEvent.Type.MEMBER_ADDED, listener3.nextEvent().type());

    assertEquals(3, cluster1.getMembershipService().getMembers().size());
    assertEquals(3, cluster2.getMembershipService().getMembers().size());
    assertEquals(3, cluster3.getMembershipService().getMembers().size());

    List<CompletableFuture<Void>> stopFutures = Stream.of(cluster1, cluster2, cluster3).map(AtomixCluster::stop)
        .collect(Collectors.toList());
    try {
      CompletableFuture.allOf(stopFutures.toArray(new CompletableFuture[stopFutures.size()])).get(10, TimeUnit.SECONDS);
    } catch (Exception e) {
      // Do nothing
    }
  }

  private class TestClusterMembershipEventListener implements ClusterMembershipEventListener {
    private BlockingQueue<ClusterMembershipEvent> queue = new ArrayBlockingQueue<ClusterMembershipEvent>(10);

    @Override
    public void onEvent(ClusterMembershipEvent event) {
      queue.add(event);
    }

    ClusterMembershipEvent nextEvent() {
      try {
        return queue.poll(10, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
        return null;
      }
    }
  }
}
