package data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class CreateRandomData {
	// map<id1, set<id2 of id1's friends>>
	private HashMap<Integer, HashSet<Integer>> relationMap;
	
	public HashMap<Integer, HashSet<Integer>> GetRelationMap() {
		return relationMap;
	}
	
	public CreateRandomData(int nodeCount, int averageEdgeOfEachNode) {
		Random r = new Random();
		
		relationMap = new HashMap<Integer, HashSet<Integer>>();

		int totalRelation = 0;
		for (int i = 0; i < nodeCount; i++) {
			int count = r.nextInt(averageEdgeOfEachNode*2) + 1;
			totalRelation += count;
			
			HashSet<Integer> friendIds = new HashSet<Integer>();
			for (int j = 0; j < count; j++) {
				int id = r.nextInt(nodeCount);
				if (friendIds.contains(id)) {
					j--;
					continue;
				} else {
					friendIds.add(id);
				}
			}
			relationMap.put(i, friendIds);
		}
			
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("randomData.txt")));
			bw.write(nodeCount + " " + totalRelation + "\n");
			
			for (int i = 0; i < nodeCount; i++) {
				HashSet<Integer> friendIds = relationMap.get(i);
				
				for (Integer id : friendIds) {
					bw.write("person" + i + " " + "person" + id + "\n");
				}
			}			

			System.out.println("total relationship: " + totalRelation);
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
