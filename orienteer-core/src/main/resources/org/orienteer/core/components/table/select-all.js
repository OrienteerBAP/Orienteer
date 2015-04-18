function installSelectAll(id)
{
	var sa = $('#'+id);
	var t = sa.closest('table');
	sa.click(function(event)
	{
		var to = this.checked;
		t.find('>tbody>tr>td.checkbox-column :checkbox').each(function(){this.checked = to});
	});
	
	t.find('>tbody>tr>td.checkbox-column :checkbox').click(function(event)
	{
		if(this.checked)
		{
			var allCb = t.find('>tbody>tr>td.checkbox-column :checkbox');
			var res = true;
			for(var i=0; i<allCb.length; i++)
			{
				if(!allCb[i].checked)
				{
					res = false;
					break;
				}
			}
			sa.prop('checked', res);
		}
		else
		{
			sa.prop('checked', false);
		}
	});
}