package jeremiah;

import org.deidentifier.arx.*;
import org.deidentifier.arx.Data.DefaultData;
import org.deidentifier.arx.aggregates.StatisticsEquivalenceClasses;
import org.deidentifier.arx.criteria.KAnonymity;
import org.deidentifier.arx.ARXResult;
import org.deidentifier.arx.metric.InformationLoss;
import org.deidentifier.arx.risk.RiskModelPopulationUniqueness;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

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

    //.......................................ANALYSIS METHODS-----------------------------------------------------//


    public static Double getLowestProsecutorRisk(ARXResult result, ARXPopulationModel pModel){
        return result.getOutput()
                .getRiskEstimator(pModel)
                .getSampleBasedReidentificationRisk()
                .getLowestRisk();
    }

    public static Double getRecodsAffectByRisk(ARXResult result, ARXPopulationModel pModel,double risk){
        return result.getOutput()
                .getRiskEstimator(pModel)
                .getSampleBasedRiskDistribution()
                .getFractionOfRecordsAtRisk(risk);

    }

    public static Double getAverageProsecutorRisk(ARXResult result, ARXPopulationModel pModel){
        return result.getOutput()
                .getRiskEstimator(pModel)
                .getSampleBasedReidentificationRisk()
                .getAverageRisk();
    }

    public static Double getHighestProsecutorRisk(ARXResult result, ARXPopulationModel pModel){
        return result.getOutput()
                .getRiskEstimator(pModel)
                .getSampleBasedReidentificationRisk()
                .getHighestRisk();
    }

    public static Double getEstimatedProsecutorRisk(ARXResult result, ARXPopulationModel pModel){
        return result.getOutput()
                .getRiskEstimator(pModel)
                .getSampleBasedReidentificationRisk()
                .getEstimatedProsecutorRisk();
    }

    public static Double getEstimatedJournalistRisk(ARXResult result, ARXPopulationModel pModel){
        return result.getOutput()
                .getRiskEstimator(pModel)
                .getSampleBasedReidentificationRisk()
                .getEstimatedJournalistRisk();
    }

    public static Double getEstimatedMarketerRisk(ARXResult result, ARXPopulationModel pModel){
        return result.getOutput()
                .getRiskEstimator(pModel)
                .getSampleBasedReidentificationRisk()
                .getEstimatedMarketerRisk();
    }

    public static Double getSampleUniques(ARXResult result, ARXPopulationModel pModel){
        return result.getOutput()
                .getRiskEstimator(pModel)
                .getSampleBasedUniquenessRisk()
                .getFractionOfUniqueTuples();
    }

    public static Double getPopulationUniques(ARXResult result, ARXPopulationModel pModel){
        return result.getOutput()
                .getRiskEstimator(pModel)
                .getPopulationBasedUniquenessRisk()
                .getFractionOfUniqueTuples(getPopulationModel(result,pModel));
    }

    public static RiskModelPopulationUniqueness.PopulationUniquenessModel getPopulationModel(ARXResult result, ARXPopulationModel pModel){
        return
                result.getOutput()
                .getRiskEstimator(pModel)
                .getPopulationBasedUniquenessRisk()
                .getPopulationUniquenessModel();
    }

    public static Set<String> getQuasiIdentifiers(ARXResult result){
        return result.getDataDefinition().getQuasiIdentifyingAttributes();
    }

    public static void showAnalysisData(ARXResult result,ARXPopulationModel pModel){
        System.out.println("Measure: Value [%]");
        System.out.println("Lowest risk: "+getLowestProsecutorRisk(result,pModel)*100);
        System.out.println("Records affected by lowest risk: " + getRecodsAffectByRisk(result,pModel,getLowestProsecutorRisk(result,pModel))*100);
        System.out.println("Average prosecutor risk: " + getAverageProsecutorRisk(result,pModel)*100);
        System.out.println("Highest prosecutor risk: " + getHighestProsecutorRisk(result,pModel)*100);
        System.out.println("Record affected by highest risk: " + getRecodsAffectByRisk(result,pModel,getHighestProsecutorRisk(result,pModel))*100);
        System.out.println("Estimated prosecutor risk: " + getEstimatedProsecutorRisk(result,pModel)*100);
        System.out.println("Estimated journalist risk: " + getEstimatedJournalistRisk(result,pModel)*100);
        System.out.println("Estimated marketer risk: " + getEstimatedMarketerRisk(result,pModel)*100);
        System.out.println("Sample uniques: " + getSampleUniques(result,pModel)*100);
        System.out.println("Population uniques: " + getPopulationUniques(result,pModel)*100);
        System.out.println("Population model: " + getPopulationModel(result,pModel));
        System.out.println("Quasi-identifiers: " + getQuasiIdentifiers(result));
    }

    //---------------------------------------------------------------------------------------------------------------//

    public static void main(String[] args) throws IOException {

       makedata();
       defineAttri();
       defineHeirarchy();
       setKAnonymity(4);
       setAnonymizer();
       File newfile = new File("C:/Users/jeuy/Desktop/bachelor/test.txt");
       result = anonymizer.anonymize(data,config);

        ARXPopulationModel pModel = ARXPopulationModel.create(data.getHandle().getNumRows(), 0.01d);

        //-------ANALYSIS METHOD CALLS ---------------------------------------------------------///
        showAnalysisData(result,pModel);

       //NodeResult(result,data,pModel);
        //-------------------------------------------------------------------------------------///

       //setter resultatet lik et datahandle objelkt for å kunne hente statisktikk bak anonymiliseringen
       handle = result.getOutput();

       //With the following code you may compute the frequency distribution of the values of an attribute in a DataHandle
       handle.getStatistics().getFrequencyDistribution(0, true);

       //With the following code you can access summary statistics:
       data.getHandle().getStatistics().getSummaryStatistics(true);


       handle.save(newfile,';');


    }
}
