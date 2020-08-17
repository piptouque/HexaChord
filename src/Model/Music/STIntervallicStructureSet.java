package Model.Music;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class STIntervallicStructureSet extends ArrayList<STIntervallicStructure> implements Set<STIntervallicStructure> {

	private static final long serialVersionUID = 1L;

	@Override
	public boolean add(STIntervallicStructure arg0) {
		if (arg0 != null && !contains(arg0)){
			super.add((STIntervallicStructure)arg0);
			return true;
		}
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends STIntervallicStructure> arg0) {
		boolean changed = false;
		for (STIntervallicStructure is : arg0){
			if (add(is)) changed=true;
		}
		return changed;
	}

	@Override
	public void clear() {
		super.clear();
	}

	@Override
	public boolean contains(Object arg0) {
		if (arg0==null) return false;
		if (arg0 instanceof STIntervallicStructure){
			for (STIntervallicStructure is : this){
				if (is.equals((STIntervallicStructure)arg0)) return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		for (Object object : arg0){
			if (!(object instanceof STIntervallicStructure) || !(contains(object))) return false;
		}
		return true;
	}

	@Override
	public boolean isEmpty() {
		return super.isEmpty();
	}

	@Override
	public Iterator<STIntervallicStructure> iterator() {
		return super.iterator();
	}

	@Override
	public boolean remove(Object arg0) {
		return super.remove(arg0);
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		return super.removeAll(arg0);
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		return super.retainAll(arg0);
	}

	@Override
	public int size() {
		return super.size();
	}

	@Override
	public Object[] toArray() {
		return super.toArray();
	}

	@Override
	public <T> T[] toArray(T[] arg0) {
		return super.toArray(arg0);
	}


	
}
