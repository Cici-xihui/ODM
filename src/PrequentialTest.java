
import com.github.javacliparser.FileOption;
import com.github.javacliparser.IntOption;
import com.yahoo.labs.samoa.instances.Prediction;
import evaluator.PrequentialMultiLabelPerformanceEvaluator;
import moa.core.InstanceExample;
import moa.core.Measurement;
import moa.core.SizeOf;
import moa.core.TimingUtils;
import moa.evaluation.LearningEvaluation;
import moa.evaluation.preview.LearningCurve;
import moa.streams.MultiTargetArffFileStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class PrequentialTest2 {

    public FileOption dumpFileOption = new FileOption("dumpFile", 'd', "File to append intermediate csv results to.", (String)null, "csv", true);
    public IntOption sampleFrequencyOption = new IntOption("sampleFrequency", 'f', "How many instances between samples of the learning performance.", 87856/100, 0, 2147483647);
                                                                                                                                                        // 19300; 87856; 13766; 1702; 43907; 13929; 120919; 3782; 2417; 28596

    private int totalInstances;

    public PrequentialTest2(int amountInstances) {

        if (amountInstances == -1) {
            this.totalInstances = 2147483647;
        } else {
            this.totalInstances = amountInstances;
        }
    }

    public void run() throws IOException {

        File dumpFile = this.dumpFileOption.getFile();
        PrintStream immediateResultStream = null;
        boolean firstDump = true;
        if (dumpFile != null) {
            try {
                if (dumpFile.exists()) {
                    immediateResultStream = new PrintStream(new FileOutputStream(dumpFile, true), true);
                } else {
                    immediateResultStream = new PrintStream(new FileOutputStream(dumpFile), true);
                }
            } catch (Exception var36) {
                throw new RuntimeException("Unable to open immediate result file: " + dumpFile, var36);
            }
        }
        LearningCurve learningCurve = new LearningCurve("learning evaluation instances");

        // preparation
        //MultiTargetArffFileStream stream = new MultiTargetArffFileStream(".././Datasets/20NG/20NG-F.arff", "20");
        MultiTargetArffFileStream stream = new MultiTargetArffFileStream(".././Datasets/Bookmarks/bookmarks.arff", "-208");
        //MultiTargetArffFileStream stream = new MultiTargetArffFileStream(".././Datasets/Corel16k/Corel16k001.arff", "-153");
        //MultiTargetArffFileStream stream = new MultiTargetArffFileStream(".././Datasets/Enron/ENRON-F.arff", "53");
        //MultiTargetArffFileStream stream = new MultiTargetArffFileStream(".././Datasets/Mediamill/mediamill.arff", "-101");
        //MultiTargetArffFileStream stream = new MultiTargetArffFileStream(".././Datasets/Ohsumed/OHSUMED-F.arff", "23");
        //MultiTargetArffFileStream stream = new MultiTargetArffFileStream(".././Datasets/Imdb/IMDB-F.arff", "28");
        //MultiTargetArffFileStream stream = new MultiTargetArffFileStream(".././Datasets/Slashdot/SLASHDOT-F.arff", "22");
        //MultiTargetArffFileStream stream = new MultiTargetArffFileStream(".././Datasets/Yeast/yeast.arff", "-14");
        //MultiTargetArffFileStream stream = new MultiTargetArffFileStream(".././Datasets/tmc2007-500/tmc2007-500.arff", "-22");

        /*MetaMultilabelGenerator stream = new MetaMultilabelGenerator();
        stream.binaryGeneratorOption.setValueViaCLIString("generators.RandomRBFGenerator -a 80"); // Tree -o 30 RBF -a 80
        stream.labelCardinalityOption.setValue(3.5); //1.8; 1.5
        stream.numLabelsOption.setValue(25);*/

        /*MetaMultilabelGenerator stream1 = new MetaMultilabelGenerator();
        stream1.binaryGeneratorOption.setValueViaCLIString("generators.RandomTreeGenerator -o 30"); // Tree -o 30 RBF -a 80
        stream1.labelCardinalityOption.setValue(1.8); //1.8; 1.5
        stream1.numLabelsOption.setValue(8); // 8; 25
        MetaMultilabelGenerator stream2 = new MetaMultilabelGenerator();
        stream2.metaRandomSeedOption.setValue(2);
        stream2.binaryGeneratorOption.setValueViaCLIString("generators.RandomTreeGenerator -o 30"); // HyperplaneGenerator
        stream2.labelCardinalityOption.setValue(1.8);
        stream2.labelDependencyChangeRatioOption.setValue(0.2);
        stream2.numLabelsOption.setValue(8);
        ConceptDriftStream stream3 = new ConceptDriftStream();
        stream3.streamOption.setCurrentObject(stream1);
        stream3.driftstreamOption.setCurrentObject(stream2);
        stream3.positionOption.setValue(250000);
        stream3.widthOption.setValue(50000);
        MetaMultilabelGenerator stream4 = new MetaMultilabelGenerator();
        stream4.metaRandomSeedOption.setValue(5);
        stream4.binaryGeneratorOption.setValueViaCLIString("generators.RandomTreeGenerator -o 30"); //RandomRBFGenerator
        stream4.labelCardinalityOption.setValue(3);
        stream4.labelDependencyChangeRatioOption.setValue(0.2);
        stream4.numLabelsOption.setValue(8);
        ConceptDriftStream stream5 = new ConceptDriftStream();
        stream5.streamOption.setCurrentObject(stream3);
        stream5.driftstreamOption.setCurrentObject(stream4);
        stream5.positionOption.setValue(500000);
        stream5.widthOption.setValue(50000);
        ConceptDriftStream stream = new ConceptDriftStream();
        stream.streamOption.setCurrentObject(stream5);
        stream.driftstreamOption.setCurrentObject(stream1);
        stream.positionOption.setValue(750000);
        stream.widthOption.setValue(50000);*/

        stream.prepareForUse();

        ODM learner = new ODM();

        learner.setModelContext(stream.getHeader());
        learner.prepareForUse();


        PrequentialMultiLabelPerformanceEvaluator evaluator = new PrequentialMultiLabelPerformanceEvaluator();
        evaluator.alphaOption.setValue(1.0);



        // Online process
        long starttime = TimingUtils.getNanoCPUTimeOfCurrentThread();
        int numberInstances = 0;
        while(numberInstances < totalInstances && stream.hasMoreInstances()) {

            InstanceExample trainInst = (InstanceExample) stream.nextInstance();
            Prediction prediction = learner.getPredictionForInstance(trainInst);

            evaluator.addResult(trainInst, prediction);

            learner.trainOnInstance(trainInst);

            if (numberInstances % (long)this.sampleFrequencyOption.getValue() == 0L && numberInstances != 0 ) {
                learningCurve.insertEntry(new LearningEvaluation(new Measurement[]{new Measurement("learning evaluation instances", (double)numberInstances)}, evaluator, learner));
                if (immediateResultStream != null) {
                    if (firstDump) {
                        immediateResultStream.println(learningCurve.headerToString());
                        firstDump = false;
                    }

                    immediateResultStream.println(learningCurve.entryToString(learningCurve.numEntries() - 1));
                    immediateResultStream.flush();
                }

                evaluator.reset();
            }

            ++numberInstances;

        }

        learningCurve.insertEntry(new LearningEvaluation(new Measurement[]{new Measurement("learning evaluation instances", (double)numberInstances)}, evaluator, learner));
        if (immediateResultStream != null) {
            if (firstDump) {
                immediateResultStream.println(learningCurve.headerToString());
                firstDump = false;
            }

            immediateResultStream.println(learningCurve.entryToString(learningCurve.numEntries() - 1));
            immediateResultStream.flush();
        }

        if (immediateResultStream != null) {
            immediateResultStream.close();
        }



        System.out.println(numberInstances + " instances.");
        long endtime = TimingUtils.getNanoCPUTimeOfCurrentThread();

        for(int i = 0; i < evaluator.getPerformanceMeasurements().length; i++)
            System.out.println(evaluator.getPerformanceMeasurements()[i].getName() + "\t" + String.format("%.3f",evaluator.getPerformanceMeasurements()[i].getValue()));

        String timeString = "Time: " + TimingUtils.nanoTimeToSeconds((endtime - starttime)) + " s  \n";
        System.out.println(timeString + "\n");
        System.out.println("Size of the model: " + SizeOf.fullSizeOf(learner) + " bytes\n");

    }

    public static void main(String[] args) throws IOException {
       PrequentialTest2 batch = new PrequentialTest2(-1); //1000000
       batch.dumpFileOption.setValue("./out/results/stationary_windows/Bookmarks/ODM.csv");
    }
}





