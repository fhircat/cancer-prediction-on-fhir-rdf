package method;

import java.io.IOException;

import data.fhir.NetworkWriter;

public class RDFtoNetwork_Writer {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String rdffile="example_merge.nt";
		String idxFile="example_merge.idx";
		String outfile="example_merge_network.txt";
		
		NetworkWriter writer=new NetworkWriter();
		writer.generateNewIndx_byRDF(rdffile, idxFile);
		writer.writeNetwork_byRDF(rdffile, idxFile, outfile);
	}

}
