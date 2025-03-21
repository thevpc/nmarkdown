package net.thevpc.nuts.lib.md.util;

import net.thevpc.nuts.lib.md.*;
import net.thevpc.nuts.lib.md.base.MdAbstractElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MdElementAndChildrenList {
    private Object frontMatter;
    List<MdElementAndChildren> currPath = new ArrayList<>();

    public MdElementAndChildrenList() {
        currPath.add(new MdElementAndChildren(new MdBody(new MdElement[0])));
    }

    public MdElement build() {
        MdElementAndChildren b = currPath.get(0).build();
        ((MdAbstractElement) b.e).setPreambleHeader(frontMatter);
        return b.e;
    }

    public MdElementAndChildren last() {
        return currPath.get(currPath.size() - 1);
    }

    public int indexOfParent(MdElement n) {
        for (int i = currPath.size() - 1; i >= 0; i--) {
            if (currPath.get(i).isParentOf(n)) {
                return i;
            }
        }
        return 0;
    }

    public void addAll(List<MdElement> all) {
        for (MdElement e : all) {
            add(e);
        }
    }

    public void add(MdElement n) {
        int i = indexOfParent(n);
        while (i < currPath.size() - 1) {
            currPath.remove(currPath.size() - 1);
        }

        MdElementAndChildren nn = new MdElementAndChildren(n);
        MdElement pp = currPath.get(i).e;
        if (n instanceof MdUnNumberedItem) {
            MdUnNumberedItem ni = (MdUnNumberedItem) n;
            if (pp instanceof MdUnNumberedList && pp.type().depth() == ni.type().depth()) {
                currPath.get(i).addChild(nn);
                currPath.add(nn);
            } else {
                int d = ni.type().depth();
                MdElementAndChildren p0 = new MdElementAndChildren(new MdUnNumberedList(d, new MdUnNumberedItem[0]));
                currPath.get(i).addChild(p0);
                currPath.add(p0);
                p0.addChild(nn);
                currPath.add(nn);
            }
        } else if (n instanceof MdNumberedItem) {
            MdNumberedItem ni = (MdNumberedItem) n;
            if (pp instanceof MdNumberedList && pp.type().depth() == ni.type().depth()) {
                currPath.get(i).addChild(nn);
                currPath.add(nn);
            } else {
                int d = ni.type().depth();
                MdElementAndChildren p0 = new MdElementAndChildren(new MdNumberedList(d, new MdNumberedItem[0]));
                currPath.get(i).addChild(p0);
                currPath.add(p0);
                p0.addChild(nn);
                currPath.add(nn);
            }
        } else {
            currPath.get(i).addChild(nn);
            currPath.add(nn);
        }

    }


    public Object getFrontMatter() {
        return frontMatter;
    }

    public void setFrontMatter(Object frontMatter) {
        this.frontMatter = frontMatter;
    }
}
