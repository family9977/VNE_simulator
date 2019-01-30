# VNE_simulator
Background: 
In network virtualization, the primary entity is the virtual network (VN), which is a combination of network nodes and network links.
Network virtualization allows multiple VNs share resources in one substrate network. The main resource allocation challenge in network
virtualization is usually referred to as the Virtual Network Embedding VNE problem. There are thousands of algorithms to solve this
resource allocation problem. Currently, there are three algorithms in this simulator.

The situation is that there are many virtual networks which are seen as requests arriving by the time, and the infrastructure provider 
needs to use an algorithm to allocate the resource of the network infrastructure to requests. The arrival of the requests follows the 
Poisson process, and the topology of a substrate network and virtual networks are randomly generated. The scale of networks, the arrival
rate of requests and the total simulation time can be set through the setting frame.
