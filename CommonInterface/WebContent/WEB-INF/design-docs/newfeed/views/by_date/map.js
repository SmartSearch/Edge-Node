function(doc) {
	if(doc.timestamp && doc.data) {
		emit(doc.timestamp, doc.data);
	}
}