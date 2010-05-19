package crl.android.pdfwriter;

public class PDFDocument extends Base {

	// the four main sections described in the PDF Reference
	private Header mHeader;
	private Body mBody;
	private CrossReferenceTable mCRT;
	private Trailer mTrailer;
	
	public PDFDocument() {
		mHeader = new Header();		
		mBody = new Body();
		mBody.setByteOffsetStart(mHeader.getPDFStringSize());
		mBody.setObjectNumberStart(0);
		mCRT = new CrossReferenceTable();
		mTrailer = new Trailer();
	}

	public IndirectObject newIndirectObject() {
		return mBody.getNewIndirectObject();
	}

	public IndirectObject newRawObject(String content) {
		IndirectObject iobj = mBody.getNewIndirectObject();
		iobj.setContent(content);
		return iobj;
	}

	public IndirectObject newDictionaryObject(String dictionaryContent) {
		IndirectObject iobj = mBody.getNewIndirectObject();
		iobj.setDictionaryContent(dictionaryContent);
		return iobj;
	}

	public IndirectObject newStreamObject(String streamContent) {
		IndirectObject iobj = mBody.getNewIndirectObject();		
		iobj.setDictionaryContent("  /Length "+Integer.toString(streamContent.length())+"\n");
		iobj.setStreamContent(streamContent);
		return iobj;
	}
	
	public void includeIndirectObject(IndirectObject iobj) {
		mBody.includeIndirectObject(iobj);
	}

	@Override
	public String toPDFString() {
		StringBuilder sb = new StringBuilder();
		sb.append(mHeader.toPDFString());
		sb.append(mBody.toPDFString());
		mCRT.setObjectNumberStart(mBody.getObjectNumberStart());
		int x = 0;
		while (x < mBody.getObjectsCount()) {
			mCRT.addObjectXRefInfo(mBody.getObjectByteOffset(x), mBody.getObjectGeneration(x), mBody.isInUseObject(x));
			x++;
		}
		mTrailer.setObjectsCount(mBody.getObjectsCount());
		mTrailer.setCrossReferenceTableByteOffset(sb.length());
		return sb.toString() + mCRT.toPDFString() + mTrailer.toPDFString();
	}
	
	@Override
	public void clear() {
		mHeader.clear();
		mBody.clear();
		mCRT.clear();
		mTrailer.clear();
	}

}
