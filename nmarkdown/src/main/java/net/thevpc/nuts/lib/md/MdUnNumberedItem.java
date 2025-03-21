/**
 * ====================================================================
 *            thevpc-common-md : Simple Markdown Manipulation Library
 * <br>
 *
 * Copyright [2020] [thevpc]
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE Version 3 (the "License");
 * you may  not use this file except in compliance with the License. You may obtain
 * a copy of the License at https://www.gnu.org/licenses/lgpl-3.0.en.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 * <br>
 * ====================================================================
 */
package net.thevpc.nuts.lib.md;

import net.thevpc.nuts.lib.md.base.MdAbstractElement;
import net.thevpc.nuts.lib.md.util.MdUtils;
import net.thevpc.nuts.util.NStringBuilder;
import net.thevpc.nuts.util.NStringUtils;

import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author thevpc
 */
public class MdUnNumberedItem extends MdAbstractElement {

    private String type;
    private String prefix;
    private MdElement value;
    private MdElementType id;
    private MdElement[] children;

    public MdUnNumberedItem(String type, String prefix,int depth, MdElement value) {
        this(type,prefix,depth,value,new MdElement[0]);
    }

    public MdUnNumberedItem(String type, String prefix,int depth, MdElement value,MdElement[] children) {
        this.type = type;
        this.value = value;
        if(!value.isInline()){
            throw new IllegalArgumentException("unexpected newline element in un-numbered item: "+value.type());
        }
        this.prefix = NStringUtils.trimRight(prefix);
        this.children = children==null?new MdElement[0] :children ;
        id=new MdElementType(MdElementTypeGroup.UNNUMBERED_ITEM,depth);
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public boolean isBlank() {
        return value.isBlank();
    }

    public MdElement[] getChildren() {
        return children;
    }

    public String getType() {
        return type;
    }

    public MdElement getValue() {
        return value;
    }

    @Override
    public MdElementType type() {
        return id;
    }

    @Override
    public String toString() {
        NStringBuilder sb=new NStringBuilder();
        char cc='-';
        if(type!=null && !type.trim().isEmpty()){
            cc=type.trim().charAt(0);
        }
        sb.append(MdUtils.times(cc, type().depth()));
        sb.append(" ").append(getValue());
        if(children!=null){
            for (MdElement child : children) {
                if(!sb.endsWith('\n') && !sb.endsWith('\r')){
                    sb.append("\n");
                }
                String ss = child.toString();
                sb.append(new NStringBuilder(ss).indent("  "));
            }
        }
        return sb.toString();
    }


    @Override
    public boolean isInline() {
        return false;
    }

    @Override
    public boolean isEndWithNewline() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MdUnNumberedItem that = (MdUnNumberedItem) o;
        return Objects.equals(type, that.type) && Objects.equals(value, that.value) && Objects.equals(id, that.id) && Arrays.equals(children, that.children);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(type, value, id);
        result = 31 * result + Arrays.hashCode(children);
        return result;
    }
}
