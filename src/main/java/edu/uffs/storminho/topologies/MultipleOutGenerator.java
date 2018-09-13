package edu.uffs.storminho.topologies;

public class MultipleOutGenerator {

	public static void main(String[] args) throws Exception {
		for(int i = 0; i < 2; i++) {
			System.out.println(i);
			OutGenerator.main(null);
			System.out.println(i);
		}

	}
}