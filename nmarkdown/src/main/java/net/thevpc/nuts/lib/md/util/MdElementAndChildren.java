package net.thevpc.nuts.lib.md.util;

import net.thevpc.nuts.lib.md.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MdElementAndChildren {
    MdElement e;
    List<MdElementAndChildren> c = new ArrayList<>();

    public MdElementAndChildren(MdElement e) {
        this.e = e;
    }

    MdElementAndChildren addChild(MdElementAndChildren i) {
        switch (e.type().group()){
            case UNNUMBERED_LIST:{
                if(i.e.type().group()!= MdElementTypeGroup.UNNUMBERED_ITEM){
                    throw new IllegalArgumentException("unexpected");
                }
                break;
            }
            case NUMBERED_LIST:{
                if(i.e.type().group()!= MdElementTypeGroup.NUMBERED_ITEM){
                    throw new IllegalArgumentException("unexpected");
                }
                break;
            }
        }
        c.add(i);
        return this;
    }

    public boolean isParentOf(MdElement child) {
        MdElement parent = e;
        MdElementTypeGroup childType = child.type().group();
        int childDepth = child.type().depth();
        MdElementTypeGroup parentType = parent.type().group();
        int parentDepth = parent.type().depth();
        switch (parentType) {
            case TITLE: {
                switch (childType) {
                    case TITLE:
                        return childDepth > parentDepth;
                    default:
                        return true;
                }
            }
            case NUMBERED_LIST: {
                switch (childType) {
                    case NUMBERED_ITEM: {
                        return childDepth > parentDepth;
                    }
                    default:
                        return false;
                }
            }
            case UNNUMBERED_LIST: {
                switch (childType) {
                    case UNNUMBERED_ITEM: {
                        if(childDepth >= parentDepth){
                            if(!c.isEmpty()){
                                MdElementAndChildren firstChild = c.get(0);
                                MdUnNumberedItem uu = firstChild.e.asUnNumItem();
                                String p = uu.getPrefix();
                                return (Objects.equals(p,child.asUnNumItem().getPrefix()));
                            }
                            return true;
                        }
                        if(MdUtils.isUnnumberedPrefixChild(child.asUnNumItem().getPrefix(),parent.asUnNumList().getPrefix())){
                            return true;
                        }
                    }
                }
                return false;
            }
            case UNNUMBERED_ITEM:{
                switch (childType) {
                    case UNNUMBERED_ITEM: {
                        if(childDepth > parentDepth){
                            return true;
                        }
                        if(MdUtils.isUnnumberedPrefixChild(child.asUnNumItem().getPrefix(),parent.asUnNumItem().getPrefix())){
                            return true;
                        }
                        break;
                    }
                    case NUMBERED_ITEM: {
                        return true;
                    }
                }
                return false;
            }
            case NUMBERED_ITEM: {
                switch (childType) {
                    case NUMBERED_ITEM: {
                        if(childDepth > parentDepth){
                            return true;
                        }
//                        if(parent.asNumItem().getPrefix().startsWith(child.asNumItem().getPrefix())){
//                            return true;
//                        }
                        break;
                    }
                    case UNNUMBERED_ITEM: {
                        return true;
                    }
                    default:
                        return false;
                }
            }
            case BODY: {
                switch (childType) {
                    case TITLE:
                        return false;
                    default:
                        return true;
                }
            }
            default: {
                return false;
            }
        }
    }

    MdElementAndChildren build() {
        if (!c.isEmpty()) {
            switch (e.type().group()) {
                case TITLE: {
                    MdTitle o = e.asTitle();
                    ArrayList<MdElement> c2 = new ArrayList<>(Arrays.asList(o.getChildren()));
                    c2.addAll(c.stream().map(x->x.build().e).collect(Collectors.toList()));
                    e = new MdTitle(
                            o.getCode(),
                            o.getValue(),
                            o.type().depth(),
                            c2.toArray(new MdElement[0])
                    );
                    c.clear();
                    return this;
                }
                case UNNUMBERED_ITEM: {
                    MdUnNumberedItem o = e.asUnNumItem();
                    ArrayList<MdElement> c2 = new ArrayList<>(Arrays.asList(o.getChildren()));
                    c2.addAll(c.stream().map(x->x.build().e).collect(Collectors.toList()));
                    e = new MdUnNumberedItem(
                            o.getType(),
                            o.getPrefix(),
                            o.type().depth(),
                            o.getValue(),
                            c2.toArray(new MdElement[0])
                    );
                    c.clear();
                    return this;
                }
                case NUMBERED_ITEM: {
                    MdNumberedItem o = e.asNumItem();
                    ArrayList<MdElement> c2 = new ArrayList<>(Arrays.asList(o.getChildren()));
                    c2.addAll(c.stream().map(x->x.build().e).collect(Collectors.toList()));
                    e = new MdNumberedItem(
                            o.getNumber(),
                            o.type().depth(),
                            o.getSep(),
                            o.getValue(),
                            c2.toArray(new MdElement[0])
                    );
                    c.clear();
                    return this;
                }
                case UNNUMBERED_LIST: {
                    MdUnNumberedList o = e.asUnNumList();
                    ArrayList<MdElement> c2 = new ArrayList<>(Arrays.asList(o.getChildren()));
                    c2.addAll(c.stream().map(x->x.build().e).collect(Collectors.toList()));
                    e = new MdUnNumberedList(
                            o.type().depth(),
                            c2.toArray(new MdUnNumberedItem[0])
                    );
                    c.clear();
                    return this;
                }
                case NUMBERED_LIST: {
                    MdNumberedList o = e.asNumList();
                    ArrayList<MdElement> c2 = new ArrayList<>(Arrays.asList(o.getChildren()));
                    c2.addAll(c.stream().map(x->x.build().e).collect(Collectors.toList()));
                    e = new MdNumberedList(
                            o.type().depth(),
                            c2.toArray(new MdNumberedItem[0])
                    );
                    c.clear();
                    return this;
                }
                case BODY: {
                    MdBody o = e.asBody();
                    ArrayList<MdElement> c2 = new ArrayList<>(Arrays.asList(o.getChildren()));
                    c2.addAll(c.stream().map(x->x.build().e).collect(Collectors.toList()));
                    e = new MdBody(c2.toArray(new MdElement[0]));
                    c.clear();
                    return this;
                }
                default:{
                    if(c.size()>0){
                        throw new IllegalArgumentException("unexpected");
                    }
                    return this;
                }
            }
        }
        return this;
    }

    @Override
    public String toString() {
        return e +"::"+c;
    }
}
