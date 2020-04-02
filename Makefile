HOME="$(shell pwd)"

mud:
	javac cs3524/solutions/mud/Edge.java; \
	javac cs3524/solutions/mud/MUD.java; \
	javac cs3524/solutions/mud/Vertex.java; \
	javac cs3524/solutions/mud/MUDServerMainLine.java; \
	javac cs3524/solutions/mud/MUDServerInterface.java; \
	javac cs3524/solutions/mud/MUDServerImpl.java; \
	javac cs3524/solutions/mud/MUDClient.java; \
	javac cs3524/solutions/mud/MUDClientInterface.java; \
	javac cs3524/solutions/mud/MUDClientMainLine.java
mudclean:
	cd cs3524/solutions/mud; \
	rm -f *.class;\
	cd $(HOME) 
	
