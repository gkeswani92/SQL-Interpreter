package indexing;

import java.io.FileNotFoundException;
import java.util.List;

import utils.DatabaseCatalog;
import utils.IndexBinaryFileWriter;

public class IndexSerializer {

	private static int pageCount;
	private static IndexBinaryFileWriter ibfw;

	public static void serializeHeader(Node root, Integer numLeaves, Integer order) {
		if(ibfw != null){
			ibfw.serializeHeader(root, numLeaves, order);
		}
	}
	
	public static void serializeLeaves(List<Node> leaves, Index index) {
		
		pageCount = 1;
		ibfw = null;
		
		try {
			ibfw = new IndexBinaryFileWriter(DatabaseCatalog.getInstance().getInputDir() + "/db/indexes/" + index.getTableName() + "." + index.getAttribute());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		if (ibfw != null) {
			for (Node node: leaves) {
				LeafNode leaf = (LeafNode)node;
				leaf.setAddress(pageCount++);
				ibfw.writeLeafNodeToFile(leaf);
			}
		}
	}
	
	public static void serializeIndexNodes(List<Node> indexNodes) {
		if(ibfw != null){
			for(Node node: indexNodes){
				IndexNode currentIndexNode = (IndexNode)node;
				currentIndexNode.setAddress(pageCount++);
				ibfw.writeIndexToFile(currentIndexNode);
			}
		}
	}
	
	
}
