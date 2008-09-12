package com.peralex.utilities.ui.graphs.scrollingline;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.peralex.utilities.locale.ILocaleListener;
import com.peralex.utilities.objectpool.GraphObjectPool;
import com.peralex.utilities.ui.graphs.lineGraph.AbstractLineGraph;
import com.peralex.utilities.ui.graphs.lineGraph.MultiLineGraph;

/**
 * A left-to-right scrolling line graph which takes (X, Y) data points.
 *
 * Assumes x values pushed into LinkedList is sorted.
 * 
 * @author Roy Emmerich
 * @author Noel Grandin
 * @author Simon Cross
 */
public class XYScrollingLineGraph extends AbstractLineGraph implements ILocaleListener {

    private static final class LineState {
        // a linked list for each channel
        public final LinkedList<Point2D.Double> data = new LinkedList<Point2D.Double>();
        public boolean visible = true;
        public Color color;
    }
    private final Map<Object, LineState> lineMap = new HashMap<Object, LineState>();
    private double fWindowSize;

    public XYScrollingLineGraph(double windowSize) {
        fWindowSize = windowSize;
    }

    public void setWindowSize(double windowSize) {
        fWindowSize = windowSize;
    }

    public double getWindowSize() {
        return fWindowSize;
    }

    /**
     * Updates the straight line graph
     */
    @Override
    protected void drawGraph(Graphics2D g) {
        // ensure we're not already drawing.
        final int currentHeight_pixels = getSize().height;
        final int currentWidth_pixels = getSize().width;

        recalculateXMinMax();

        /** Conversion ratio between units and pixels */
        final double yPlotRatio = currentHeight_pixels / (getMaximumY() - getMinimumY());
        final double xPlotRatio = currentWidth_pixels / (getMaximumX() - getMinimumX());
        synchronized (lineMap) {
            for (LineState state : this.lineMap.values()) {
                paintLine(g, currentHeight_pixels, yPlotRatio, xPlotRatio, state);
            }
        }
    }

    private void paintLine(Graphics2D g, final int currentHeight_pixels, final double yPlotRatio, final double xPlotRatio, LineState state) {
        if (state.visible) {
            final LinkedList<Point2D.Double> currentList = state.data;

            if (!currentList.isEmpty()) {
                final int[] aiXCoordinates = GraphObjectPool.checkOutIntArray(currentList.size());
                final int[] aiYCoordinates = GraphObjectPool.checkOutIntArray(currentList.size());

                int j = 0;
                for (Point2D.Double p : currentList) {
                    aiXCoordinates[j] = (int) ((p.x - this.getMinimumX()) * xPlotRatio);
                    aiYCoordinates[j] = (int) (currentHeight_pixels - ((p.y - this.getMinimumY()) * yPlotRatio));
                    j++;
                }

                g.setColor(state.color);
                g.drawPolyline(aiXCoordinates, aiYCoordinates, currentList.size());

                GraphObjectPool.checkIn(aiXCoordinates);
                GraphObjectPool.checkIn(aiYCoordinates);
            }
        }
    }

    /**
     * Method for adding data for a specific line.
     */
    public void addLineValue(Object key, double xValue, double yValue) {
        synchronized (lineMap) {
            ensureKeyExists(key);
            final LineState state = lineMap.get(key);

            // ensure data ordering.
            if (!state.data.isEmpty() && xValue <= state.data.getFirst().x) {
              throw new IllegalStateException("cannot add an X value <= than the previous X value " 
              		+ xValue + "<=" + state.data.getFirst().x);
            }

            state.data.addFirst(new Point2D.Double(xValue, yValue));            
            while (!state.data.isEmpty() && state.data.getFirst().x - state.data.getLast().x > fWindowSize) {
                state.data.removeLast();
            }
        }
        if (!bFrameRepaintLimited) {
            repaint();
        }
    }

    /**
     * clear the data for a line.
     */
    public void clearLine(Object key) {
        boolean repaintNeeded = false;
        synchronized (lineMap) {
            ensureKeyExists(key);
            final LineState state = lineMap.get(key);
            if (state.data.size() > 0) {
                repaintNeeded = true;
            }
            state.data.clear();
        }
        if (repaintNeeded) {
            if (!bFrameRepaintLimited) {
                repaint();
            }
        }
    }

    /**
     * Method for pushing a whole map of lines.
     */
    public void addLineValues(Map<Object, Point2D.Double> newData) {
        synchronized (lineMap) {
            for (Object key : newData.keySet()) {
                ensureKeyExists(key);
                final LineState state = lineMap.get(key);

                final Point2D.Double val = newData.get(key);
                state.data.addFirst(val);
                while (!state.data.isEmpty() && state.data.getFirst().x - state.data.getLast().x > fWindowSize) {
                    state.data.removeLast();
                }
            }
        }
        if (!bFrameRepaintLimited) {
            repaint();
        }
    }

    /**
     * Multiple graphs can be drawn on this DrawSurface. Each graph or signal must be able to be made in/visible upon
     * request.
     * 
     * @param visible - True to see the signal, false to make it invisible
     */
    public void setLineVisible(Object key, boolean visible) {
        boolean repaintNeeded = false;
        synchronized (lineMap) {
            ensureKeyExists(key);
            final LineState state = lineMap.get(key);
            if (!state.visible == visible) {
                repaintNeeded = true;
            }
            state.visible = visible;
        }
        if (repaintNeeded) {
            repaint();
        }
    }

    /**
     * sets the visibility of all the lines
     */
    public void setLinesVisible(boolean visible) {
        boolean repaintNeeded = false;
        synchronized (lineMap) {
            for (LineState state : lineMap.values()) {
                if (!state.visible == visible) {
                    repaintNeeded = true;
                }
                state.visible = visible;
            }
        }
        if (repaintNeeded) {
            repaint();
        }
    }

    public void setLineColor(Object key, Color color) {
        boolean repaintNeeded = false;
        synchronized (lineMap) {
            ensureKeyExists(key);
            final LineState state = lineMap.get(key);
            if (!state.color.equals(color)) {
                repaintNeeded = true;
            }
            state.color = color;
        }
        if (repaintNeeded) {
            repaint();
        }
    }

    /**
     * Clear the current data of the graph.
     */
    @Override
    public void clear() {
        boolean repaintNeeded = false;
        synchronized (lineMap) {
            for (LineState state : lineMap.values()) {
                if (state.data.size() > 0) {
                    repaintNeeded = true;
                }
                state.data.clear();
            }
        }
        if (repaintNeeded) {
            repaint();
        }
    }

    private void ensureKeyExists(Object key) {
        synchronized (lineMap) {
            if (!lineMap.containsKey(key)) {
                lineMap.put(key, new LineState());
                lineMap.get(key).color = MultiLineGraph.allocateLineColor(lineMap.size());
            }
        }
    }

    private void recalculateXMinMax() {
        double maxX = -Double.MAX_VALUE;
        synchronized (lineMap) {
            if (lineMap.isEmpty()) {
                resetZoom();
                return;
            }
            for (LineState lineState : lineMap.values()) {
                final LinkedList<Point2D.Double> oLineData = lineState.data;
                if (!oLineData.isEmpty()) {
                    maxX = Math.max(maxX, oLineData.getFirst().x);
                }
            }
        }
        setGridXMinMax(maxX - fWindowSize, maxX);
    }

    @Override
    protected void autoScaleGraph() {
        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;

    		boolean foundOne = false;
        synchronized (lineMap) {
            for (LineState lineState : lineMap.values()) {
        				if (!lineState.visible) continue;
                final LinkedList<Point2D.Double> oLineData = lineState.data;
                for (Point2D.Double p : oLineData) {
                		foundOne = true;
                    minY = Math.min(minY, p.y);
                    maxY = Math.max(maxY, p.y);
                    minX = Math.min(minX, p.x);
                    maxX = Math.max(maxX, p.x);
                }
            }
        }
    		if (foundOne) 
    		{
          zoomIn(minX, maxX, minY, maxY);
    		}
    		else
    		{
    			resetZoom();
    		}
    }
}
