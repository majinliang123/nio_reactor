# NIO Reactor

### About
- A Nio server, use multi reactor pattern like netty.
- Could process 10,000 request in 9 seconds.

### Design
- There is a acceptor to listen connection from client
- when acceptor get a connection, it will dispatch to processors
- There are a lot of processors to process a connection. And each processor corresponding to a thread.
- Every socket channel from acceptor will be registered on the selector of processor. 
