/**
 * ====================================================================
 * thevpc-common-md : Simple Markdown Manipulation Library
 * <br>
 * <p>
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

import net.thevpc.nuts.lib.md.util.MdUtils;
import net.thevpc.nuts.util.NAssert;
import net.thevpc.nuts.util.NBlankable;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author thevpc
 */
public class MdUnNumberedList extends MdParent {
    private MdElementType id;
    private String prefix;

    public MdUnNumberedList(MdUnNumberedItem[] content) {
        super(content);
        id = new MdElementType(MdElementTypeGroup.UNNUMBERED_LIST, content[0].type().depth());
        if (content.length > 1) {
            String p = content[0].getPrefix();
            for (int i = 1; i < content.length; i++) {
                MdUnNumberedItem c = content[i];
                String p2 = c.getPrefix();
                NAssert.requireTrue(Objects.equals(p, p2), "equals " + p + " and " + p2);
            }
            this.prefix = p;
        } else {
            this.prefix = "";
        }
    }

    public MdUnNumberedList(int depth, MdUnNumberedItem[] content) {
        super(content);
        id = new MdElementType(MdElementTypeGroup.UNNUMBERED_LIST, depth);
        if (content.length > 1) {
            String p = content[0].getPrefix();
            for (int i = 1; i < content.length; i++) {
                MdUnNumberedItem c = content[i];
                String p2 = c.getPrefix();
                if(!Objects.equals(p, p2)){
                    boolean u = MdUtils.isUnnumberedPrefixChild(p2, p);
                    System.out.println("why");
                }
                NAssert.requireTrue(Objects.equals(p, p2), "expected '" + p + "' and got '" + p2+"'");
            }
        } else {
            this.prefix = "";
        }
    }

    public String getPrefix() {
        return prefix;
    }

    public MdElementType type() {
        return id;
    }

    public boolean isInline() {
        return false;
    }

    @Override
    public boolean isEndWithNewline() {
        return true;
    }

    @Override
    public MdUnNumberedItem[] getChildren() {
        return (MdUnNumberedItem[]) super.getChildren();
    }

    @Override
    public MdUnNumberedItem getChild(int i) {
        return (MdUnNumberedItem) super.getChild(i);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size(); i++) {
            if (i > 0) {
                if (!isInline()) {
                    sb.append("\n");
                }
            }
            sb.append(getChild(i));
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MdUnNumberedList that = (MdUnNumberedList) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }

    @Override
    public boolean isBlank() {
        return (getChildren() == null
                || getChildren().length == 0
                || Arrays.stream(getChildren()).allMatch(x -> NBlankable.isBlank(x)));
    }
}
