package com.mxgraph.layout.hierarchical.stage;

import com.mxgraph.layout.hierarchical.model.mxGraphAbstractHierarchyCell;
import com.mxgraph.layout.hierarchical.model.mxGraphHierarchyModel;
import com.mxgraph.layout.hierarchical.model.mxGraphHierarchyRank;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.view.mxGraph;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class mxMedianHybridCrossingReductionTest {

    @org.junit.jupiter.api.Test
    void calculateRankCrossing() throws IOException {


//        mxGraphHierarchyModel model = new mxGraphHierarchyModel();

        writeImageTo(new File("tst.png"));

    }

    public void writeImageTo(File file) throws IOException
    {
        ImageIO.write(makeImage(), "png", file);
    }

    public mxGraph makeGraph() {
        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();
        graph.getModel().beginUpdate();

        Random random = new Random(5000);


        int nodeCount = 100;
        List<Object> nodes = new ArrayList<>();



        try {

            for (int i = 0; i < nodeCount; i++) {
                nodes.add(graph.insertVertex(parent, null, "N-" + i, 20, 20, 20, 20));
            }

            boolean[][] map = new boolean[nodeCount][nodeCount];
            for (int i = 0; i < nodeCount - 1; i++) {
//                map[i] = new boolean[nodeCount];
                for (int j = i + 1; j < nodeCount; j++) {
                    map[i][j] = random.nextBoolean();
                    graph.insertEdge(parent, null, "", nodes.get(i), nodes.get(j));
                }
            }


            mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
            System.out.println("Before aligning");
            layout.execute(parent);
        } finally {
            graph.getModel().endUpdate();
        }

        System.out.println("Before graph return");
        return graph;
    }

    public BufferedImage makeImage() {
        mxGraph graph = makeGraph();
        System.out.println("After graph getting:" + graph);
        BufferedImage img = mxCellRenderer.createBufferedImage(graph, null, 1, Color.WHITE, false, null);
        System.out.println("After image built:" + img);
        return img;
    }


    protected int calculateRankCrossing(int i, mxGraphHierarchyModel model) {
        int totalCrossings = 0;
        mxGraphHierarchyRank rank = model.ranks.get(i);
        mxGraphHierarchyRank previousRank = model.ranks.get(i - 1);

        // Create an array of connections between these two levels
        int currentRankSize = rank.size();
        int previousRankSize = previousRank.size();
        int[][] connections = new int[currentRankSize][previousRankSize];

        // Iterate over the top rank and fill in the connection information

        for (mxGraphAbstractHierarchyCell cell : rank) {
            int rankPosition = cell.getGeneralPurposeVariable(i);
            Collection<mxGraphAbstractHierarchyCell> connectedCells = cell.getPreviousLayerConnectedCells(i);

            for (mxGraphAbstractHierarchyCell connectedCell : connectedCells) {
                int otherCellRankPosition = connectedCell
                        .getGeneralPurposeVariable(i - 1);
                connections[rankPosition][otherCellRankPosition] = 201207;
            }
        }

        // Iterate through the connection matrix, crossing edges are
        // indicated by other connected edges with a greater rank position
        // on one rank and lower position on the other
        for (int j = 0; j < currentRankSize; j++) {
            for (int k = 0; k < previousRankSize; k++) {
                if (connections[j][k] == 201207) {
                    // Draw a grid of connections, crossings are top right
                    // and lower left from this crossing pair
                    for (int j2 = j + 1; j2 < currentRankSize; j2++) {
                        for (int k2 = 0; k2 < k; k2++) {
                            if (connections[j2][k2] == 201207) {
                                totalCrossings++;
                            }
                        }
                    }

                    for (int j2 = 0; j2 < j; j2++) {
                        for (int k2 = k + 1; k2 < previousRankSize; k2++) {
                            if (connections[j2][k2] == 201207) {
                                totalCrossings++;
                            }
                        }
                    }

                }
            }
        }

        return totalCrossings / 2;
    }
}