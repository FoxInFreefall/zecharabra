/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tilor;

import java.util.ArrayList;

/**
 *
 * @author seanlanghi
 */
public interface TilorDelegate
{
    public void framepathListDidUpdate(ArrayList<String> framepaths);
    public void frameIconAtIndexDidMoveToIndex(int oldIndex, int newIndex);
    public String getRelativePathFromProjectHomeDir(String path);
    public String getAbsPathOfRelPath(String relPath);
}
