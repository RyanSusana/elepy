package com.elepy.di.tree;

import jakarta.inject.Inject;

public class Node1 {
    @Inject
    Node2 node2;
    @Inject
    Node3 node3;
}
