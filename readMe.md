Cancer Prediction based on FHIR and RDF
===============================================

This study presents a method to utilize the phenotypic (EHR-based) and genetic (genetic reports) data features to classify primary cancer and cancers of unknown primary (CUP).  

## Framework

We presented a network-based framework (Figure 1) that represented cancer data using the FHIR standard and RDF to facilitate the cancer prediction process. Five types of data sources extracted from the EHR: “genetic information”, “lab tests”, “diagnosis”, “medication”, and “family historical records”, were represented with FHIR resources and then converted to the RDF-based representation. A graph embedding algorithm, Node2vec, was used to provide a vectorial representation of nodes in the resulting network as well as bag of feature (BOF) to form the features for the classification models.

![alt tag](https://github.com/fhircat/cancer-prediction-on-fhir-rdf/blob/master/figure-1.png)

Figure 1, framework


##  Data preprocessing and data modeling based on FHIR and RDF

We used the FHIR-based data model to represent the data elements in genetic reports and structured EHR data. Specially, we represented “genetic entries” with the existing profile Observation-genetics extended from the resource Observation. The “lab test”, “diagnosis”, and “medication” entries were represented with the resources Observation, Condition, Medication that identified with encounters (e.g., billing and EHR encounters) and service date. The “family historical records” entities were represented with the resource FamilyMemberHistory as the diseases and were encoded with the attribute condition. All the resources were associated with the resource Patient. We further converted the JSON formatted FHIR data to RDF format. An example of data representation based on FHIR and RDF can be found in Figure 2. 

![alt tag](https://github.com/fhircat/cancer-prediction-on-fhir-rdf/blob/master/fhir2rdf_example_100.png)

Figure 2, example of FHIR2RDF

## Tools

### ARFF generation from matrix
method.InputMatrix_generator.java

### FHIR and RDF representation
method.FHIRtoRDF_Writer.java
method.RDFtoNetwork_Writer.java

### Prediction
method.Classification.java

