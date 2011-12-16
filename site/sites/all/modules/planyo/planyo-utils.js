// $Id$

function planyo_get_next_month(month, year) {
  var next_month = month + 1;
  var next_year = year;
  if (next_month == 13) {
    next_month = 1;
    next_year++;
  }
  return new Array(next_month, next_year);
}

function planyo_get_prev_month(month, year) {
  var prev_month = month - 1;
  var prev_year = year;
  if (prev_month == 0) {
    prev_month = 12;
    prev_year--;
  }
  return new Array(prev_month, prev_year);
}

function planyo_get_month_specs (month, year) {
  // returns an array: (offset of the first day of month, # of days in last month, # of days in current month, month, year)
      
  var d = new Date();
  if (!month || !year)
    d.setFullYear(d.getFullYear (), d.getMonth (), 1);
  else
    d.setFullYear(year, month - 1, 1);
  var first_weekday = planyo_isset(document.first_weekday) ? document.first_weekday : 1;
  var first_offset = (7 - first_weekday + d.getDay()) % 7;
  var last_month_last_date = new Date(d);
  last_month_last_date.setDate(d.getDate()-1);
  var prev_month_count = last_month_last_date.getDate();
  var this_month_last_date = new Date(d);
  this_month_last_date.setMonth(d.getMonth() + 1);
  this_month_last_date.setDate(this_month_last_date.getDate() - 1);
  var this_month_count = this_month_last_date.getDate();
  return new Array(first_offset, prev_month_count, this_month_count, month, year);
}

function planyo_get_day_name(n, is_short) {
  var first_weekday = planyo_isset(document.first_weekday) ? document.first_weekday : 1;
  // day names are imported from planyo.com using the chosen language; only if no translations are found, the English versions are used
  var arr = planyo_isset(document.s_weekdays_short) ? (is_short ? document.s_weekdays_short : document.s_weekdays_med) :
    (is_short ? new Array("M", "T", "W", "T", "F", "S", "S") : new Array("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"));
  return arr [(n + first_weekday + 6) % 7];
}

function planyo_get_month_name(n, is_short) {
  // month names are imported from planyo.com using the chosen language; only if no translations are found, the English versions are used
  var arr = planyo_isset(document.s_months_short) ? (is_short ? document.s_months_short : document.s_months_long) :
    (is_short ? new Array("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec") : new Array("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"));
  return arr [n - 1];
}

function planyo_output_hour_only(hour, european_style_postfix) {
  if (document.time_format && document.time_format.indexOf('a') != -1)
    return ((hour % 12) == 0 ? 12 : hour % 12) + ' '+ (hour < 12 ? 'am' : 'pm');
  return hour + (european_style_postfix ? european_style_postfix : '');
}

function planyo_output_time(hour, minute) {
  var time_str = document.time_format;
  if (!time_str)
    time_str = "H:i";
  time_str = time_str.replace("H", hour);
  time_str = time_str.replace("h", (hour % 12) == 0 ? 12 : hour % 12);
  time_str = time_str.replace("a", hour < 12 ? 'am' : 'pm');
  time_str = time_str.replace("i", minute < 10 ? '0' + minute : minute);
  return time_str;
}

function planyo_output_date(year, month, day) {
  var date = document.date_format;
  if (!date)
    date = "Y-m-d";
  date = date.replace("Y", year);
  date = date.replace("m", month < 10 ? '0'+month : month);
  date = date.replace("M", planyo_get_month_name(month, true));
  date = date.replace("d", day < 10 ? '0' + day : day);
  return date;
}

function planyo_get_day_info_for_month(month, year) {
  var now = new Date();

  var day_info = new Array();
  
  var month_specs = planyo_get_month_specs(month, year);
  var day_iterator = month_specs [1] - month_specs [0] + 1;
  var days_in_month_left = month_specs [0] - 1;
  var month_iterator = -1;
  if (days_in_month_left == -1) {
    month_iterator = 0;
    days_in_month_left = month_specs [2] - 1;
    day_iterator = 1;
  }
  for (var y = 0; y < 6; y++) {
    day_info [y] = new Array();
    for (var x = 0; x < 7; x++) {
      day_info [y][x] = new Array();
      var day_class;
      if (month - 1 == now.getMonth() && month_iterator == 0 && day_iterator == now.getDate() && year == now.getFullYear ())
	day_class = 'active_day';
      else
	day_class = (month_iterator == 0 ? 'cur_month_day' : 'ext_month_day');

      day_info[y][x]['type'] = day_class;
      day_info[y][x]['day'] = day_iterator;
      day_info[y][x]['month'] = month + month_iterator;
      day_info[y][x]['year'] = year;
      if (month + month_iterator == 0) {
	day_info[y][x]['month'] = 12;
	day_info[y][x]['year'] = year - 1;
      } else if (month + month_iterator == 13) {
	day_info[y][x]['month'] = 1;
	day_info[y][x]['year'] = year + 1;
      }
      
      if (days_in_month_left == 0) {
	month_iterator++;
	days_in_month_left = month_specs [2];
	day_iterator = 0;
      }
      days_in_month_left--;
      day_iterator++;
    }
  }

  return day_info;
}

function planyo_show_calendar_picker(month, year, div_id, date_fun) {
  var now = new Date();
  if (month == null && year == null) {
    month = now.getMonth() + 1;
    year = now.getFullYear();
  }
  else if (month == 0) {
    month = 12;
    year--;
  }
  else if (month == 13) {
    month = 1;
    year++;
  }
  var day_info = planyo_get_day_info_for_month(month, year);
  
  var div_code = "<table class='calpicker'><caption><a class='nav' href=\"javascript:planyo_show_calendar_picker(" + (month-1) + ", " + year + ", '" + div_id + "','" + date_fun + "');\">&laquo;</a><a class='nav' href=\"javascript:planyo_show_calendar_picker(" + (month + 1) + "," + year + ", '" + div_id + "','" + date_fun + "');\">&raquo;</a> " + planyo_get_month_name(month, false) + " " + year + "</caption><thead><tr>";
  for (var i = 0; i < 7; i++) {
    div_code += "<th>" + planyo_get_day_name(i, true) + "</th>";
  }
  div_code += "</thead><tbody>";
  for (var y = 0; y < 6; y++) {
    div_code += "<tr>";
    for (var x = 0; x < 7; x++) {
      div_code += "<td class='" + day_info[y][x]['type'] + "' onclick='" + date_fun + "(" + day_info[y][x]['day'] + "," + day_info[y][x]['month'] + "," + day_info[y][x]['year'] + ")'>" + day_info[y][x]['day'] + "</td>";
    }
    div_code += "</tr>";
  }
  div_code += "</tr>";
  div_code += "</tbody></table>";
  jQuery('#' + div_id).html(div_code);
}

function planyo_get_prev_day(day, month, year, offset) {
  // returns an array: (day, month, year)
  // note: offset must be < 28
  
  var specs = planyo_get_month_specs(month, year);

  var ret_val = new Array ();
  if (!offset) offset = 1;
  if (day - offset <= 0) {
    ret_val[0] = specs[1] - offset + day;
    ret_val[1] = month - 1;
    ret_val[2] = year;
    if (ret_val[1] == 0) {
      ret_val[1] = 12;
      ret_val[2] = year - 1;
    }
  }
  else {
    ret_val[0] = day - offset;
    ret_val[1] = month;
    ret_val[2] = year;
  }
  return ret_val;
}

function planyo_get_next_day(day, month, year, offset) {
  // returns an array: (day, month, year)
  // note: offset must be < 28
  
  var specs = planyo_get_month_specs(month, year);

  var ret_val = new Array ();
  if (!offset) offset = 1;
  if (day + offset > specs [2]) {
    ret_val[0] = day + offset - specs [2];
    ret_val[1] = month + 1;
    ret_val[2] = year;
    if (ret_val[1] == 13) {
      ret_val[1] = 1;
      ret_val[2] = year + 1;
    }
  }
  else {
    ret_val[0] = day + offset;
    ret_val[1] = month;
    ret_val[2] = year;
  }
  return ret_val;
}

// returns an array (min, max) of min and max values of array
// if array's values are an array of properties, property to be used can be specified
// otherwise, leave property null
function planyo_get_array_min_max(arr, property) {
  var min;
  var max;
  var n = 0;
  for (var it in arr) {
    var val = (property ? it [property] : it);
    if (n == 0 || val < min)
      min = val;
    if (n == 0 || val > max)
      max = val;
    n++;
  }
  return new Array(min,max);
}

function planyo_confirm_action_with_input(text) {
  return prompt(text);
}

function planyo_confirm_action(text) {
  if (confirm(text))
    return true;
  return false;
}

function planyo_isset(obj, d1, d2, d3, d4, d5) {
  try {
    if (d5 != null)
      return obj[d1][d2][d3][d4][d5] != undefined;
    if (d4 != null)
      return obj[d1][d2][d3][d4] != undefined;
    if (d3 != null)
      return obj[d1][d2][d3] != undefined;
    if (d2 != null)
      return obj[d1][d2] != undefined;
    if (d1 != null)
      return obj[d1] != undefined;
    return obj != undefined;
  }
  catch (err) {
  }
  return false;
}

function planyo_close_calendar() {
  if (document.current_picker) {
    var el = jQuery('#' + document.current_picker + 'cal');
    el.css('visibility', 'hidden');
    el.css('left', '-1000');
  }
}

function convert_entities_to_utf8 (str) {
  var i = str.indexOf ("&#");
  while (i != -1) {
    var iSC = str.indexOf (";", i);
    if (iSC != -1 && (iSC - i - 2) > 0 && (iSC - i - 2) <= 5) {
      var decimal = str.substr (i + 2, iSC - i - 2);
      if (!isNaN (decimal) && decimal == parseInt (decimal)) {
        str = str.substr (0, i) + String.fromCharCode (decimal) + ((str.length > iSC + 1) ? str.substr (iSC + 1) : '');
      }
    }
    i = str.indexOf ("&#", i + 1);
  }  
  return str;
}

function planyo_calendar_date_chosen(day, month, year) {
  var picker = jQuery('#' + document.current_picker);
  picker.val(convert_entities_to_utf8 (planyo_output_date(year, month, day)));
  if (document.current_picker_onchange)
    eval(document.current_picker_onchange);
  document.previous_month_picked = month;
  document.previous_year_picked = year;
  planyo_close_calendar();
  if (window.js_nav) {
    if (document.current_picker == 'start_date')
      js_nav(null, month, year);
    else if (document.current_picker == 'one_date' || document.current_picker == 'date')
      js_nav(day, month, year);
  }
}

function planyo_show_calendar(cal,onchange) {
  var cal_el = jQuery('#' + cal);
  var cal_ref_el = jQuery('#' + cal + 'calref');
  var old_date = Date.parse(cal_el.val());
  if (!cal_el.val()) {
    if (!document.current_picker) {
      if (jQuery('#start_date'))
        document.current_picker = 'start_date';
      else if (jQuery('#one_date'))
        document.current_picker = 'one_date';
    }
    var picker = jQuery('#' + document.current_picker);
    if (picker)
      old_date = Date.parse(picker.val());
  }
  document.current_picker = cal;
  document.current_picker_onchange = onchange;
  var month = null;
  var year = null;
  if (document.previous_year_picked != 'undefined' && document.previous_month_picked != 'undefined') {
    month = document.previous_month_picked;
    year = document.previous_year_picked;
  }
  if (old_date != 'undefined' && old_date > 0) {
    var old_date_obj = new Date();
    old_date_obj.setTime(old_date);
    month = old_date_obj.getMonth() + 1;
    year = old_date_obj.getFullYear();
  }
  var cal_cal_el = jQuery('#' + cal + 'cal');
  if (cal_cal_el.parent() != jQuery('body')) 
    jQuery('body').append(cal_cal_el);
  planyo_show_calendar_picker (month, year, cal + 'cal', 'planyo_calendar_date_chosen');
  cal_cal_el.css('left', cal_ref_el.offset().left + 'px');
  cal_cal_el.css('top', (cal_ref_el.offset().top + cal_ref_el.height() + 5) + 'px');
  cal_cal_el.css('visibility', 'visible');
}

function planyo_get_param(name) {
  name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
  var regexS = "[\\?&]"+name+"=([^&#]*)";
  var regex = new RegExp(regexS);
  var results = regex.exec(window.location.href);
  if (results == null)
    return null;
  else
    return results[1];
}

function get_full_planyo_file_path(name) {
  var loc = window.planyo_files_location;
  if (!loc)
    loc = planyo_settings.planyo_files_location;
  if (loc.length == 0 || loc.lastIndexOf('/') == loc.length - 1)
    return loc + name;
  else
    return loc + '/' + name;
}

function show_product_images (parent_, id) {
  var box = jQuery('#product_box_' + id);
  var parent = jQuery (parent_);
  if (box && parent) {    
    box.css('top', parent.offset().top + 'px');
    box.css('left', (parent.offset().left + 30) + 'px');
    box.css('display', '');
  }  
}

function hide_product_images (id) {
  var box = jQuery ('#product_box_' + id);
  if (box) {
    box.css('display', 'none');
  }
}
