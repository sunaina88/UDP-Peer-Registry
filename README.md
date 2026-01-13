# 🌐 UDP Peer Registry & P2P Messaging System

> Discover peers. Look them up. Message them directly.

This Java project implements a **UDP-based peer discovery and communication system**.  
Peers register with a central registry server, fetch the list of online peers, look up specific users, and send **direct peer-to-peer messages** — all without a persistent connection.

---

## 🚀 Features

- Central **UDP peer registry** (name → IP:port mapping)  
- Client self-registration with unique usernames  
- Fetch list of all online peers (`LIST`)  
- Look up exact peer address (`LOOKUP:<name>`)  
- Direct P2P messaging using UDP  
- Concurrent listener thread for incoming messages  
- Lightweight, no TCP connection required  

---

## 🛠️ Project Structure

```bash
udp-peer-registry/
├── PeerRegistry.java # Central registry server
├── PeerClient.java # Peer node that registers & communicates
└── README.md # You're here right now :)
```
---

## 🏁 How to Run

Make sure you're inside the project folder.

### 1. Compile all Java files

```bash
javac *.java
```

### 2. Start the Registry Server
```bash
java PeerRegistry
```

You should see:

```bash
Peer Registry started on port 7777
Listening for REGISTER, LIST, LOOKUP commands...
```

### 3. Start a Peer Client

Open a second terminal:

```bash
java PeerClient
```

You’ll be asked for a username:

```bash
Enter username: alice
Registered as 'alice' on port 8123
```

### 4. Start another Peer

Open a third terminal:

```bash
java PeerClient
```

Give it another username:

```bash
Enter username: bob
Registered as 'bob' on port 8712
```
---

## 💬 Available Commands

Inside any PeerClient terminal:

📌 List all online peers
```bash
LIST
```

🔎 Look up a specific peer's address
```bash
LOOKUP:bob
```

Registry replies:

```bash
PEER:bob:127.0.0.1:8712
```

📩 Send a direct message

```bash
MSG:bob:hello there!
```

Bob receives:

```bash
[Direct from alice] hello there!
```

🚪 Quit the client
```bash
QUIT
```
---

## 📸 Example Interaction

alice terminal: 
```bash
LIST
PEER:alice:127.0.0.1:8123
PEER:bob:127.0.0.1:8712

MSG:bob:hi Bob!
[Direct to bob] hi Bob!
```

bob terminal: 

```bash
[Direct from alice] hi Bob!
```
---

## 🧠 What This Project Demonstrates

- UDP communication (DatagramSocket + DatagramPacket)
- Decentralized peer discovery
- Custom text-based network protocol
- Mapping peers to dynamic ports
- Writing concurrent listeners in Java
- Client–server & P2P interaction patterns

---

## 🙋‍♀️ Author

**sunaina ☀**  (she/her)

GitHub: [@sunaina88](https://github.com/sunaina88)

---

Feel free to ⭐ the repo if you found it useful or fun!
