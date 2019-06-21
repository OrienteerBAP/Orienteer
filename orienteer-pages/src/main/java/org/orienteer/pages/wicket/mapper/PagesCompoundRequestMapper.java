package org.orienteer.pages.wicket.mapper;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.request.IRequestMapper;
import org.apache.wicket.request.mapper.CompoundRequestMapper;
import org.orienteer.pages.module.PagesModule;
import org.orienteer.pages.repository.OPageRepository;

/**
 * Main {@link IRequestMapper} for Orienteer Pages
 */
public class PagesCompoundRequestMapper extends CompoundRequestMapper {

	public PagesCompoundRequestMapper() {
		super();
		initialPagesLoad();
	}
	
	protected void initialPagesLoad() {
		OPageRepository.getPages()
				.stream()
				.filter(p -> p.field(PagesModule.OPROPERTY_PATH) != null)
				.forEach(this::add);
	}

	public PagesCompoundRequestMapper add(ODocument page) {
		super.add(new PagesMountedMapper(page));
		return this;
	}

	public PagesCompoundRequestMapper remove(ODocument page) {
		for (IRequestMapper mapper : this) {
			if (isServing(mapper, page)) {
				remove(mapper);
				break;
			}
		}
		return this;
	}

	private boolean isServing(IRequestMapper mapper, ODocument page) {
		return mapper instanceof PagesMountedMapper && ((PagesMountedMapper) mapper).isServing(page.getIdentity());
	}
}
