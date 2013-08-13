/**
 * Ext.CopyButton - Clipboard Copy Button
 * Version 2.0
 * Oral Dalay orald@vbi.vt.edu (v2.0)
 * James Dempster letssurf@gmail.com (v1.0)
 */

/**
 * @class Ext.ux.CopyButton
 * @extends Ext.Button
 * @constructor
 * @param {Object} config
 */
Ext.define('Ext.CopyButton', {

	extend : 'Ext.button.Button',
	alias : 'widget.copybutton',
	/**
	 * @cfg {String} value The value which is copied to the clipboard when the button is pressed
	 */

	// private
	initComponent : function() {
		// Ext.CopyButton.superclass.initComponent.call(this);
		this.cls = this.ctCls;
		this.clip = new ZeroClipboard.Client();
	},

	// private
	afterRender : function() {

		// Ext.CopyButton.superclass.afterRender.call(this);
		this.clip.glue(this.getEl().dom);
		this.clip.hide();

		var func = Ext.bind(this.clipMouseOver, this);
		this.clip.addEventListener('mouseOver', func);
		func = Ext.bind(this.clipMouseOut, this);
		this.clip.addEventListener('mouseOut', func);
		func = Ext.bind(this.clipMouseDown, this);
		this.clip.addEventListener('mouseDown', func);
		func = Ext.bind(this.clipMouseUp, this);
		this.clip.addEventListener('mouseUp', func);
	},

	// private
	onDestroy : function() {
		this.clip.destroy();
	},

	// private
	onMouseOver : function() {
		if (!this.disabled) {
			this.clip.show();
			// this.el.addCls('x-btn-over');
		}
	},

	// private
	clipMouseDown : function() {
		this.clip.setText(this.getValue());
		this.el.addCls('x-btn-click');
		Ext.getDoc().on('mouseup', this.clipMouseUp, this);
	},

	// private
	clipMouseUp : function() {
		Ext.getDoc().un('mouseup', this.clipMouseUp, this);
		// this.el.removeCls('x-btn-click');
		// this.el.removeCls('x-btn-over');
		this.focus();
		this.fireEvent('click', this);

	},

	// private
	clipMouseOver : function() {
		if (!this.disabled) {
			// this.el.addCls('x-btn-over');
			this.fireEvent('mouseover', this);
		}
	},

	// private
	clipMouseOut : function() {
		this.clip.hide();
		// this.el.removeCls('x-btn-over');
		if (!this.disabled) {
			this.fireEvent('mouseout', this);
		}
	},

	getClip : function() {
		return this.clip;
	},
	/**
	 * Sets the value that should be used when the button is pressed
	 * @param {String} value The value to set
	 */
	setValue : function(value) {
		this.value = String(value);
	},

	/**
	 * Get the value that will be used when the user clicks the button
	 * @return {String} value The value used to copy to the clipboard
	 */
	getValue : function() {
		return this.value;
	}
}); 