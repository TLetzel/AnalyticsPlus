/*
 * Copyright 2015-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.hswt.anap.ui.vaadin.common;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;

/**
 * Taken from Vaadin Chart demo.
 * 
 * @author luthardt
 */
public class PointReduceUtil {

    /**
     * Reduce data series with bit more sophisticated method.
     *
     * @param series
     *            with x y data pairs
     * @param pixels
     */
    public void ramerDouglasPeuckerReduce(DataSeries series, int pixels) {
        // Calculate rough estimate for visual ratio on x-y, might be bad guess
        // if axis have been set manually
        DataSeriesItem dataSeriesItem = series.get(0);
        double xMax = dataSeriesItem.getX().doubleValue();
        double xMin = xMax;
        double yMax = dataSeriesItem.getY().doubleValue();
        double yMin = yMax;
        for (int i = 1; i < series.size(); i++) {
            DataSeriesItem item = series.get(i);
            double x = item.getX().doubleValue();
            if (xMax < x) {
                xMax = x;
            }
            if (xMin > x) {
                xMin = x;
            }
            double y = item.getY().doubleValue();
            if (yMax < y) {
                yMax = y;
            }
            if (yMin > y) {
                yMin = y;
            }
        }
        double xyRatio = (xMax - xMin) / (yMax - yMin);

        // rough estimate for sane epsilon (1px)
        double epsilon = (xMax - xMin) / pixels;

        List<DataSeriesItem> rawData = series.getData();
        List<DataSeriesItem> reduced = ramerDouglasPeucker(rawData, epsilon,
                xyRatio);
        series.setData(reduced);

    }
    /**
    *
    *
    * @param points
    * @param epsilon
    * @param xyRatio
    *            y values are multiplied with this to make distance calculation
    *            in algorithm sane
    * @return
    */
   private List<DataSeriesItem> ramerDouglasPeucker(
           List<DataSeriesItem> points, double epsilon, final double xyRatio) {
       // Find the point with the maximum distance
       double dmax = 0;
       int index = 0;
       DataSeriesItem start = points.get(0);
       DataSeriesItem end = points.get(points.size() - 1);

       for (int i = 1; i < points.size() - 1; i++) {
           DataSeriesItem point = points.get(i);
           double d = pointToLineDistance(start, end, point, xyRatio);
           if (d > dmax) {
               index = i;
               dmax = d;
           }
       }

       List<DataSeriesItem> reduced = new ArrayList<DataSeriesItem>();
       if (dmax >= epsilon) {
           // max distance is greater than epsilon, keep the most relevant
           // point and recursively simplify
           List<DataSeriesItem> startToRelevant = ramerDouglasPeucker(
                   points.subList(0, index + 1), epsilon, xyRatio);
           reduced.addAll(startToRelevant);
           List<DataSeriesItem> relevantToEnd = ramerDouglasPeucker(
                   points.subList(index, points.size() - 1), epsilon, xyRatio);
           reduced.addAll(relevantToEnd.subList(1, relevantToEnd.size()));
       } else {
           // no relevant points, drop all but ends
           reduced.add(start);
           reduced.add(end);
       }

       return reduced;
   }
   
   private double pointToLineDistance(DataSeriesItem A,
           DataSeriesItem B, DataSeriesItem P, final double xyRatio) {
       double bY = B.getY().doubleValue() * xyRatio;
       double aY = A.getY().doubleValue() * xyRatio;
       double pY = P.getY().doubleValue() * xyRatio;
       double normalLength = Math.hypot(B.getX().doubleValue()
               - A.getX().doubleValue(), bY - aY);
       return Math.abs((P.getX().doubleValue() - A.getX().doubleValue())
               * (bY - aY) - (pY - aY)
               * (B.getX().doubleValue() - A.getX().doubleValue()))
               / normalLength;
   }
}
