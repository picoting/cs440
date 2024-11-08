package src.labs.zomtree.agents;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
// SYSTEM IMPORTS
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


// JAVA PROJECT IMPORTS
import edu.bu.labs.zomtree.agents.SurvivalAgent;
import edu.bu.labs.zomtree.features.quality.Entropy;
import edu.bu.labs.zomtree.features.Features.FeatureType;
import edu.bu.labs.zomtree.linalg.Matrix;
import edu.bu.labs.zomtree.utils.Pair;



public class DecisionTreeAgent
    extends SurvivalAgent
{

    public static class DecisionTree
        extends Object
    {

        // an abstract Node type. This is extended to make Interior Nodes and Leaf Nodes
        public static abstract class Node
            extends Object
        {

            // the dataset that was used to construct this node
            private Matrix X;
            private Matrix y_gt;

            public Node(Matrix X, Matrix y_gt)
            {
                this.X = X;
                this.y_gt = y_gt;
            }

            public final Matrix getX() { return this.X; }
            public final Matrix getY() { return this.y_gt; }

            // a method to get the majority class (i.e. the most popular class) from ground truth.
            public int getMajorityClass(Matrix X, Matrix y_gt)
            {
                Pair<Matrix, Matrix> uniqueYGtAndCounts = y_gt.unique();
                Matrix uniqueYGtVals = uniqueYGtAndCounts.getFirst();
                Matrix counts = uniqueYGtAndCounts.getSecond();

                // find the argmax of the counts
                int rowIdxOfMaxCount = -1;
                double maxCount = Double.NEGATIVE_INFINITY;

                for(int rowIdx = 0; rowIdx < counts.getShape().getNumRows(); ++rowIdx)
                {
                    if(counts.get(rowIdx, 0) > maxCount)
                    {
                        rowIdxOfMaxCount = rowIdx;
                        maxCount = counts.get(rowIdx, 0);
                    }
                }

                return (int)uniqueYGtVals.get(rowIdxOfMaxCount, 0);
            }

            // an abstract method to predict the class for this example
            public abstract int predict(Matrix x);

            // an abstract method to get the datasets that each child node should be built from
            public abstract List<Pair<Matrix, Matrix> > getChildData() throws Exception;

        }

        // leaf node type
        public static class LeafNode
            extends Node
        {

            // a leaf node has the class label inside it
            private int predictedClass;

            public LeafNode(Matrix X, Matrix y_gt)
            {
                super(X, y_gt);
                this.predictedClass = this.getMajorityClass(X, y_gt);
            }

            @Override
            public int predict(Matrix x)
            {
                // predict the class (an integer)
                return this.predictedClass;
            }

            @Override
            public List<Pair<Matrix, Matrix> > getChildData() throws Exception { return null; }

        }

        // interior node type
        public static class InteriorNode
            extends Node
        {

            // the column index of the feature that this interior node has chosen
            private int             featureIdx;

            // the type (continuous or discrete) of the feature this interior node has chosen
            private FeatureType     featureType;

            // when we're processing a discrete feature, it is possible that even though that discrete feature
            // can take on any value in its domain (for example, like 5 values), the data we have may not contain
            // all of those values in it. Therefore, whenever we want to predict a test point, it is possible
            // that the test point has a discrete value that we haven't seen before. When we encounter such scenarios
            // we should predict the majority class (aka assign an "out-of-bounds" leaf node)
            private int             majorityClass;

            // the values of the feature that identify each child
            // if the feature this node has chosen is discrete, then |splitValues| = |children|
            // if the feature this node has chosen is continuous, then |splitValues| = 1 and |children| = 2
            private List<Double>    splitValues; 
            private List<Node>      children;

            // what features are the children of this node allowed to use?
            // this is different if the feature this node has chosen is discrete or continuous
            private Set<Integer>    childColIdxs;

            public InteriorNode(Matrix X, Matrix y_gt, Set<Integer> availableColIdxs)
            {
                super(X, y_gt);
                this.splitValues = new ArrayList<Double>();
                this.children = new ArrayList<Node>();
                this.majorityClass = this.getMajorityClass(X, y_gt);

                // make a deepcopy of the set that is given to us....we need to potentially remove stuff from this
                // so don't use a shallow copy and risk messing up parent nodes (with a shared shallow copy)!
                this.childColIdxs = new HashSet<Integer>(availableColIdxs);

                // quite a lot happens in this method.
                // this method will figure out which feature (amongst all the ones that we are allowed to see)
                // has the "best" quality (as measured by info gain). It will also populate the field 'this.splitValues'
                // with the correct values for that feature.
                // (side note: this is why this method is being called *after* this.splitValues is initialized)
                this.featureIdx = this.pickBestFeature(X, y_gt, availableColIdxs);
                this.featureType = DecisionTree.FEATURE_HEADER[this.getFeatureIdx()];

                // once we know what feature this node has, we need to remove that feature from our children
                // if that feature is discrete.
                // we made a deepcopy of the set so we're all good to in-place remove here.
                if(this.getFeatureType().equals(FeatureType.DISCRETE))
                {
                    this.getChildColIdxs().remove(this.getFeatureIdx());
                }
            }

            //------------------------ some getters and setters (cause this is java) ------------------------
            public int getFeatureIdx() { return this.featureIdx; }
            public final FeatureType getFeatureType() { return this.featureType; }

            private List<Double> getSplitValues() { return this.splitValues; }
            private List<Node> getChildren() { return this.children; }

            public Set<Integer> getChildColIdxs() { return this.childColIdxs; }
            public int getMajorityClass() { return this.majorityClass; }
            //-----------------------------------------------------------------------------------------------

            // make sure we add children in the correct order when we use this!
            public void addChild(Node n) { this.getChildren().add(n); }


            // TODO: complete me!
            private int pickBestFeature(Matrix X, Matrix y_gt, Set<Integer> availableColIdxs)
            {
                return -1;
            }

            // TODO: complete me!
            private Pair<Double, Matrix> getConditionalEntropy(Matrix X, Matrix y_gt, int colIdx) throws Exception
            {
                return null;
            }

            // TODO: complete me!
            @Override
            public int predict(Matrix x)
            {
                return -1;
            }

            // TODO: complete me!
            @Override
            public List<Pair<Matrix, Matrix> > getChildData() throws Exception
            {
                return null;
            }

        }

        public Node root;

        // the feature types for this game in the order of the columns (i.e. first column is CONTINUOUS, last is DISCRETE, etc.)
        public static final FeatureType[] FEATURE_HEADER = {FeatureType.CONTINUOUS,
                                                            FeatureType.CONTINUOUS,
                                                            FeatureType.DISCRETE,
                                                            FeatureType.DISCRETE};

        public DecisionTree()
        {
            this.root = null;
        }

        public Node getRoot() { return this.root; }
        private void setRoot(Node n) { this.root = n; }

        // TODO: complete me!
        private Node dfsBuild(Matrix X, Matrix y_gt, Set<Integer> availableColIdxs) throws Exception
        {
            return null;
        }

        public void fit(Matrix X, Matrix y_gt)
        {
            System.out.println("DecisionTree.fit: X.shape=" + X.getShape() + " y_gt.shape=" + y_gt.getShape());
            try
            {
                //save matrices to disk
                //System.out.println("saving matrices to disk");
                //saveMatrixToDisk(X, "X_matrix.txt");
                //saveMatrixToDisk(y_gt, "y_gt_matrix.txt");

                Set<Integer> allColIdxs = new HashSet<Integer>();
                for(int colIdx = 0; colIdx < X.getShape().getNumCols(); ++colIdx)
                {
                    allColIdxs.add(colIdx);
                }
                this.setRoot(this.dfsBuild(X, y_gt, allColIdxs));


            } catch(Exception e)
            {
                e.printStackTrace();
                System.exit(-1);
            }
        }

        private void saveMatrixToDisk(Matrix matrix, String filename) {
            try (FileOutputStream fileOut = new FileOutputStream(filename);
                 ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
                out.writeObject(matrix.toStringData());
                System.out.println("Matrix saved to " + filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public int predict(Matrix x)
        {
            // class 0 means Human (i.e. not a zombie), class 1 means zombie
            System.out.println("DecisionTree.predict: x=" + x);
            return this.getRoot().predict(x);
        }

    }

    private DecisionTree tree;

    public DecisionTreeAgent(int playerNum, String[] args)
    {
        super(playerNum, args);
        this.tree = new DecisionTree();
    }

    public DecisionTree getTree() { return this.tree; }

    @Override
    public void train(Matrix X, Matrix y_gt)
    {
        System.out.println(X.getShape() + " " + y_gt.getShape());
        this.getTree().fit(X, y_gt);
    }

    @Override
    public int predict(Matrix featureRowVector)
    {
        return this.getTree().predict(featureRowVector);
    }

}
