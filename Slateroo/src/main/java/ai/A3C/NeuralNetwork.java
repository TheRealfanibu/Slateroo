package ai.A3C;

import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.rl4j.network.ac.ActorCriticFactoryCompGraphStdDense;
import org.deeplearning4j.rl4j.network.ac.ActorCriticLoss;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.MultiDataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.RmsProp;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;
import java.io.IOException;

public class NeuralNetwork {
    private ComputationGraph network;

    public NeuralNetwork() {
        a3cBuild();
    }

    private void a3cBuild() {
        int hidden1Neurons = 64;
        int hidden2Neurons = 16;

        ComputationGraphConfiguration config = new NeuralNetConfiguration.Builder()
                .seed(AIConstants.RANDOM_SEED)
                .updater(new RmsProp(AIConstants.LEARNING_RATE, AIConstants.RMS_PROP_DECAY,
                        RmsProp.DEFAULT_RMSPROP_EPSILON))
                .graphBuilder()
                .addInputs("input")
                .addLayer("Hidden1", new DenseLayer.Builder()
                                .activation(Activation.RELU)
                                .nIn(AIConstants.NUM_STATES)
                                .nOut(hidden1Neurons)
                                .build(), "input")
                .addLayer("Hidden2", new DenseLayer.Builder()
                        .activation(Activation.RELU)
                        .nIn(hidden1Neurons)
                        .nOut(hidden2Neurons)
                        .build(), "Hidden1")
                .addLayer("Policy", new OutputLayer.Builder()
                        .lossFunction(new ActorCriticLoss())
                        .activation(Activation.SOFTMAX)
                        .nIn(hidden2Neurons)
                        .nOut(AIConstants.NUM_ACTIONS).build(), "Hidden2")
                .addLayer("Value", new OutputLayer.Builder()
                        .lossFunction(LossFunctions.LossFunction.MSE)
                        .activation(Activation.IDENTITY)
                        .nIn(hidden2Neurons)
                        .nOut(1).build(), "Hidden2")


                .setOutputs("Policy", "Value")
                .build();

        network = new ComputationGraph(config);
        network.init();
        network.setListeners(new ScoreIterationListener(10));
    }

    public INDArray[] predict(double[][] states) {
        INDArray ndStates = Nd4j.create(states);
        return network.output(ndStates);
    }

    public INDArray[] predict(double[] state) {
        INDArray ndState = Nd4j.create(state);
        return network.output(ndState);
    }

    public void fit(double[][] states, double[][] onehotActionLabels, INDArray valueLabels) {
        INDArray[] inputs = {Nd4j.create(states)};
        INDArray[] labels = {Nd4j.create(onehotActionLabels), valueLabels};
        network.fit(inputs, labels);
    }

    public void save() {
        try {
            network.save(new File(System.getProperty("user.dir") + "src/main/resources/ai/Slateroo-model.zip"));
        } catch (IOException e) {
            throw new RuntimeException("Save model failed: " + e);
        }
    }

    /*private void mnistBuild() {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .weightInit(WeightInit.XAVIER)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(new Nesterovs())
        return network.output(Nd4j.create(states));
    }

    public void fit(double[] states) {
        network.
    }

    public Evaluation evaluate(DataSetIterator testData) {
                .l2(1e-4)
                .list()
                .layer(0, new DenseLayer.Builder()
                        .nIn(784)
                        .nOut(200)
                        .activation(Activation.RELU)
                        .build())
                .layer(1, new OutputLayer.Builder()
                        .nIn(200)
                        .nOut(10)
                        .activation(Activation.SOFTMAX)
                        .build())
                .backprop(true).pretrain(false).build();

        network = new MultiLayerNetwork(conf);
        network.setListeners(new ScoreIterationListener(100), new PerformanceListener(100));
    }*/
}


