# Akka_IOT
This project creates an IOT system using Akka framework

# Actor Structure

![](./resources/images/arch_tree_diagram.png)

# Best practices of Akka Actor system

## Distributed Application
In a distributed application the actors might communicate
with each other on the local JVM or with a remote actor.

With Remote actor, there are overheads associated:
1. Network bandwidth issue
2. Size of the payload
3. Security

While the local actors just pass the object reference around
which is much faster but not scalable.

Both the cases are susceptible to message loss due 
to an actor dieing or message lost in network.
Even though in both cases, the service recovers after a while (the actor is restarted by its supervisor, the host is restarted by an operator or by a monitoring system) individual requests are lost during the crash. Therefore, writing your actors such that every message could possibly be lost is the safe, pessimistic bet.  