HOME="$(shell pwd)"

mud:
	javac mud/Edge.java; \
	javac mud/MUD.java; \
	javac mud/Vertex.java; \
	javac mud/MUDServerMainLine.java; \
	javac mud/MUDServerInterface.java; \
	javac mud/MUDServerImpl.java; \
	javac mud/MUDClient.java; \
	javac mud/MUDClientInterface.java; \
	javac mud/MUDClientMainLine.java
mudclean:
	cd mud; \
	rm -f *.class;\
	cd $(HOME) 
	
