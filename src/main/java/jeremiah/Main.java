package jeremiah;

import org.apache.poi.ss.formula.functions.T;
import org.deidentifier.arx.*;
import org.deidentifier.arx.Data.DefaultData;
import org.deidentifier.arx.aggregates.StatisticsEquivalenceClasses;
import org.deidentifier.arx.criteria.KAnonymity;
import org.deidentifier.arx.ARXResult;
import org.deidentifier.arx.metric.InformationLoss;
import org.deidentifier.arx.risk.RiskModelAttributes;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Main {
    static DefaultData data = Data.create();

    static ARXConfiguration config = ARXConfiguration.create();

    static ARXAnonymizer anonymizer = new ARXAnonymizer();

    static ARXResult result;

    static DataHandle handle;


    public static void makedata() {
        data.add("age", "gender", "zipcode");
        data.add("34", "male", "81667");
        data.add("35", "female", "81668");
        data.add("36", "male", "81669");
        data.add("37", "female", "81670");
        data.add("38", "male", "81671");
        data.add("39", "female", "81672");
        data.add("40", "male", "81673");
        data.add("41", "female", "81674");
        data.add("42", "male", "81675");
        data.add("43", "female", "81676");
        data.add("44", "male", "81677");
    }

    public static void defineAttri(){

        //Defining attribute types(sensitive, identifying, quasi-identifying, insensitive, etc)
        data.getDefinition().setAttributeType("age", AttributeType.IDENTIFYING_ATTRIBUTE);
        data.getDefinition().setAttributeType("gender", AttributeType.QUASI_IDENTIFYING_ATTRIBUTE);
        data.getDefinition().setAttributeType("zipcode", AttributeType.QUASI_IDENTIFYING_ATTRIBUTE);
    }

    public static void defineHeirarchy(){

        AttributeType.Hierarchy.DefaultHierarchy hierarchy = AttributeType.Hierarchy.create();
        hierarchy.add("81667", "8166*", "816**", "81***", "8****", "*****");
        hierarchy.add("81668", "8166*", "816**", "81***", "8****", "*****");
        hierarchy.add("81669", "8166*", "816**", "81***", "8****", "*****");
        hierarchy.add("81670", "8167*", "816**", "81***", "8****", "*****");
        hierarchy.add("81671", "8167*", "816**", "81***", "8****", "*****");
        hierarchy.add("81672", "8167*", "816**", "81***", "8****", "*****");
        hierarchy.add("81673", "8167*", "816**", "81***", "8****", "*****");
        hierarchy.add("81674", "8167*", "816**", "81***", "8****", "*****");
        hierarchy.add("81675", "8167*", "816**", "81***", "8****", "*****");
        hierarchy.add("81676", "8167*", "816**", "81***", "8****", "*****");
        hierarchy.add("81677", "8167*", "816**", "81***", "8****", "*****");

        data.getDefinition().setAttributeType("zipcode", hierarchy);


    }

    public static void setKAnonymity(int k){
        config.addPrivacyModel(new KAnonymity(k));
        config.setSuppressionLimit(0.02d);

    }

    public static void setAnonymizer(){
        anonymizer.setMaximumSnapshotSizeDataset(0.2);
        anonymizer.setMaximumSnapshotSizeSnapshot(0.2);
        anonymizer.setHistorySize(200);
    }

    public static void NodeResult(ARXResult result,Data dataAll){
        ARXLattice.ARXNode node = result.getGlobalOptimum();
        String test = Arrays.toString(node.getTransformation());
        System.out.println("- Solution ");
        System.out.println(test);

        ARXPopulationModel pModel =
                ARXPopulationModel.create(dataAll.getHandle().getNumRows(),
                        0.01d);

        double riskP = result.getOutput()
                .getRiskEstimator(pModel)
                .getSampleBasedReidentificationRisk()
                .getEstimatedProsecutorRisk();

        double riskJ = result.getOutput()
                .getRiskEstimator(pModel)
                .getSampleBasedReidentificationRisk()
                .getEstimatedJournalistRisk();

        double riskM = result.getOutput()
                .getRiskEstimator(pModel)
                .getSampleBasedReidentificationRisk()
                .getEstimatedMarketerRisk();

        System.out.println("  * Prosecutor re-identification risk: " + riskP);
        System.out.println("  * Journalist re-identification risk: " + riskJ);
        System.out.println("  * Marketer   re-identification risk: " + riskM);

        InformationLoss<?> vo = result.getGlobalOptimum().getLowestScore();
        InformationLoss<?> v1 = result.getGlobalOptimum().getHighestScore();
        System.out.println("- Information Loss " );
        System.out.println(vo+"/"+v1);


        StatisticsEquivalenceClasses v2 = result.getOutput(result.getGlobalOptimum(), false).getStatistics().getEquivalenceClassStatistics();
        System.out.println("- Statistics");
        System.out.println(v2);

        String v3 = "";
        v3 = v3 + dataAll.getHandle().getView().getNumRows();
        v3 = v3 + " records with ";
        v3 = v3 + dataAll.getDefinition().getQuasiIdentifyingAttributes().size();
        v3 = v3 + " quasi-identifiers";
        System.out.println("- Data: ");
        System.out.println(v3);

        String v4 = String.valueOf(result.getLattice().getSize());
        System.out.println("- Policies available ");
        System.out.println(v4);

    }


    public static void main(String[] args) throws IOException {

       makedata();
       defineAttri();
       defineHeirarchy();
       setKAnonymity(4);
       setAnonymizer();
       File newfile = new File("C:/Users/jeuy/Desktop/bachelor/test.txt");
       result = anonymizer.anonymize(data,config);

       NodeResult(result,data);

       //setter resultatet lik et datahandle objelkt for å kunne hente statisktikk bak anonymiliseringen
       handle = result.getOutput();

       //With the following code you may compute the frequency distribution of the values of an attribute in a DataHandle
       handle.getStatistics().getFrequencyDistribution(0, true);

       //With the following code you can access summary statistics:
       data.getHandle().getStatistics().getSummaryStatistics(true);


       handle.save(newfile,';');


    }
}
